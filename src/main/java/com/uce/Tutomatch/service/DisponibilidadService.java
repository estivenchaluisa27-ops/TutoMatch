package com.uce.Tutomatch.service;

import com.uce.Tutomatch.model.Disponibilidad;
import com.uce.Tutomatch.model.PerfilTutor;
import com.uce.Tutomatch.repository.DisponibilidadRepository;
import com.uce.Tutomatch.repository.PerfilTutorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
public class DisponibilidadService {

    private final DisponibilidadRepository disponibilidadRepository;
    private final PerfilTutorRepository perfilTutorRepository;

    public DisponibilidadService(DisponibilidadRepository disponibilidadRepository,
                                 PerfilTutorRepository perfilTutorRepository) {
        this.disponibilidadRepository = disponibilidadRepository;
        this.perfilTutorRepository = perfilTutorRepository;
    }

    public PerfilTutor obtenerPerfilPorUsuarioId(Long usuarioId) {
        return perfilTutorRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de tutor no encontrado"));
    }

    public List<Disponibilidad> obtenerDisponibilidades(Long usuarioId) {
        PerfilTutor perfil = obtenerPerfilPorUsuarioId(usuarioId);
        return disponibilidadRepository.findByPerfilTutorIdOrderByDiaSemanaAscHoraInicioAsc(perfil.getId());
    }

    @Transactional
    public Disponibilidad agregarBloque(Long usuarioId, Integer diaSemana, LocalTime horaInicio, LocalTime horaFin) {
        if (diaSemana < 1 || diaSemana > 7) {
            throw new IllegalArgumentException("El día debe estar entre 1 (lunes) y 7 (domingo)");
        }
        if (!horaInicio.isBefore(horaFin)) {
            throw new IllegalArgumentException("La hora de inicio debe ser anterior a la hora de fin");
        }
        if (horaInicio.getMinute() % 30 != 0 || horaFin.getMinute() % 30 != 0) {
            throw new IllegalArgumentException("Los bloques deben ser en incrementos de 30 minutos");
        }

        PerfilTutor perfil = obtenerPerfilPorUsuarioId(usuarioId);

        boolean solapado = disponibilidadRepository
                .findByPerfilTutorIdOrderByDiaSemanaAscHoraInicioAsc(perfil.getId())
                .stream()
                .anyMatch(b -> b.getDiaSemana().equals(diaSemana)
                        && b.getEstado() == Disponibilidad.EstadoDisponibilidad.LIBRE
                        && horaInicio.isBefore(b.getHoraFin())
                        && horaFin.isAfter(b.getHoraInicio()));
        if (solapado) {
            throw new IllegalArgumentException("El bloque se solapa con uno existente");
        }

        Disponibilidad bloque = new Disponibilidad(perfil, diaSemana, horaInicio, horaFin);
        return disponibilidadRepository.save(bloque);
    }

    @Transactional
    public void eliminarBloque(Long usuarioId, Long bloqueId) {
        Disponibilidad bloque = disponibilidadRepository.findById(bloqueId)
                .orElseThrow(() -> new IllegalArgumentException("Bloque no encontrado"));
        PerfilTutor perfil = obtenerPerfilPorUsuarioId(usuarioId);

        if (!bloque.getPerfilTutor().getId().equals(perfil.getId())) {
            throw new IllegalArgumentException("No puedes eliminar un bloque que no te pertenece");
        }

        disponibilidadRepository.delete(bloque);
    }
}
