package com.uce.Tutomatch.service;

import com.uce.Tutomatch.model.BloqueSemanal;
import com.uce.Tutomatch.model.Disponibilidad;
import com.uce.Tutomatch.model.PerfilTutor;
import com.uce.Tutomatch.model.TutorMateria;
import com.uce.Tutomatch.repository.BloqueSemanalRepository;
import com.uce.Tutomatch.repository.DisponibilidadRepository;
import com.uce.Tutomatch.repository.PerfilTutorRepository;
import com.uce.Tutomatch.repository.TutorMateriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SemanalDisponibilidadService {

    private final BloqueSemanalRepository bloqueSemanalRepository;
    private final DisponibilidadRepository disponibilidadRepository;
    private final PerfilTutorRepository perfilTutorRepository;
    private final TutorMateriaRepository tutorMateriaRepository;

    public SemanalDisponibilidadService(BloqueSemanalRepository bloqueSemanalRepository,
                                        DisponibilidadRepository disponibilidadRepository,
                                        PerfilTutorRepository perfilTutorRepository,
                                        TutorMateriaRepository tutorMateriaRepository) {
        this.bloqueSemanalRepository = bloqueSemanalRepository;
        this.disponibilidadRepository = disponibilidadRepository;
        this.perfilTutorRepository = perfilTutorRepository;
        this.tutorMateriaRepository = tutorMateriaRepository;
    }

    private void sincronizarDisponibilidad(Long perfilTutorId, List<BloqueSemanal> bloques) {
        disponibilidadRepository.deleteByPerfilTutorIdAndEstado(
                perfilTutorId, Disponibilidad.EstadoDisponibilidad.LIBRE);
        for (BloqueSemanal b : bloques) {
            if (b.getEstado() != BloqueSemanal.EstadoBloque.DISPONIBLE) continue;
            Disponibilidad d = new Disponibilidad(
                    b.getPerfilTutor(),
                    b.getDiaSemana(),
                    LocalTime.of(b.getHora(), 0),
                    LocalTime.of(b.getHora() + 1, 0)
            );
            disponibilidadRepository.save(d);
        }
    }

    public PerfilTutor obtenerPerfilPorUsuarioId(Long usuarioId) {
        return perfilTutorRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de tutor no encontrado"));
    }

    public Map<Integer, Set<Integer>> obtenerSemana(Long usuarioId, LocalDate semanaInicio) {
        PerfilTutor perfil = obtenerPerfilPorUsuarioId(usuarioId);
        List<BloqueSemanal> bloques = bloqueSemanalRepository
                .findByPerfilTutorIdAndSemanaInicioOrderByDiaSemanaAscHoraAsc(perfil.getId(), semanaInicio);
        return bloques.stream()
                .filter(b -> b.getEstado() == BloqueSemanal.EstadoBloque.DISPONIBLE)
                .collect(Collectors.groupingBy(
                        BloqueSemanal::getDiaSemana,
                        Collectors.mapping(BloqueSemanal::getHora, Collectors.toSet())
                ));
    }

    public Map<String, List<Long>> obtenerMateriasPorBloque(Long usuarioId, LocalDate semanaInicio) {
        PerfilTutor perfil = obtenerPerfilPorUsuarioId(usuarioId);
        List<BloqueSemanal> bloques = bloqueSemanalRepository
                .findByPerfilTutorIdAndSemanaInicioOrderByDiaSemanaAscHoraAsc(perfil.getId(), semanaInicio);
        Map<String, List<Long>> result = new HashMap<>();
        for (BloqueSemanal b : bloques) {
            String key = b.getDiaSemana() + "-" + b.getHora();
            result.put(key, b.getMaterias().stream()
                    .map(TutorMateria::getId)
                    .collect(Collectors.toList()));
        }
        return result;
    }

    public List<TutorMateria> obtenerTutorMaterias(Long usuarioId) {
        PerfilTutor perfil = obtenerPerfilPorUsuarioId(usuarioId);
        return perfil.getMaterias();
    }

    @Transactional
    public void guardarSemana(Long usuarioId, LocalDate semanaInicio,
                               Map<Integer, Map<Integer, List<Long>>> celdasActivas) {
        PerfilTutor perfil = obtenerPerfilPorUsuarioId(usuarioId);
        Long perfilId = perfil.getId();

        bloqueSemanalRepository.deleteByPerfilTutorIdAndSemanaInicio(perfilId, semanaInicio);

        List<BloqueSemanal> nuevosBloques = new ArrayList<>();
        for (Map.Entry<Integer, Map<Integer, List<Long>>> entryDia : celdasActivas.entrySet()) {
            Integer diaSemana = entryDia.getKey();
            for (Map.Entry<Integer, List<Long>> entryHora : entryDia.getValue().entrySet()) {
                Integer hora = entryHora.getKey();
                List<Long> materiaIds = entryHora.getValue();
                if (hora < 7 || hora > 19) continue;
                BloqueSemanal bloque = new BloqueSemanal(perfil, semanaInicio, diaSemana, hora);
                if (materiaIds != null && !materiaIds.isEmpty()) {
                    List<TutorMateria> materias = tutorMateriaRepository.findAllById(materiaIds);
                    bloque.setMaterias(materias);
                }
                bloqueSemanalRepository.save(bloque);
                nuevosBloques.add(bloque);
            }
        }

        sincronizarDisponibilidad(perfilId, nuevosBloques);
    }

    @Transactional
    public void limpiarSemana(Long usuarioId, LocalDate semanaInicio) {
        PerfilTutor perfil = obtenerPerfilPorUsuarioId(usuarioId);
        bloqueSemanalRepository.deleteByPerfilTutorIdAndSemanaInicio(perfil.getId(), semanaInicio);
        disponibilidadRepository.deleteByPerfilTutorIdAndEstado(
                perfil.getId(), Disponibilidad.EstadoDisponibilidad.LIBRE);
    }
}
