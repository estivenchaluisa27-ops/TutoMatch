package com.uce.Tutomatch.service;

import com.uce.Tutomatch.model.BloqueSemanal;
import com.uce.Tutomatch.model.PerfilTutor;
import com.uce.Tutomatch.repository.BloqueSemanalRepository;
import com.uce.Tutomatch.repository.PerfilTutorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SemanalDisponibilidadService {

    private final BloqueSemanalRepository bloqueSemanalRepository;
    private final PerfilTutorRepository perfilTutorRepository;

    public SemanalDisponibilidadService(BloqueSemanalRepository bloqueSemanalRepository,
                                        PerfilTutorRepository perfilTutorRepository) {
        this.bloqueSemanalRepository = bloqueSemanalRepository;
        this.perfilTutorRepository = perfilTutorRepository;
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

    @Transactional
    public void guardarSemana(Long usuarioId, LocalDate semanaInicio,
                               Map<Integer, Set<Integer>> celdasActivas) {
        PerfilTutor perfil = obtenerPerfilPorUsuarioId(usuarioId);
        Long perfilId = perfil.getId();

        bloqueSemanalRepository.deleteByPerfilTutorIdAndSemanaInicio(perfilId, semanaInicio);

        for (Map.Entry<Integer, Set<Integer>> entry : celdasActivas.entrySet()) {
            Integer diaSemana = entry.getKey();
            for (Integer hora : entry.getValue()) {
                if (hora < 7 || hora > 19) continue;
                BloqueSemanal bloque = new BloqueSemanal(perfil, semanaInicio, diaSemana, hora);
                bloqueSemanalRepository.save(bloque);
            }
        }
    }

    @Transactional
    public void limpiarSemana(Long usuarioId, LocalDate semanaInicio) {
        PerfilTutor perfil = obtenerPerfilPorUsuarioId(usuarioId);
        bloqueSemanalRepository.deleteByPerfilTutorIdAndSemanaInicio(perfil.getId(), semanaInicio);
    }
}
