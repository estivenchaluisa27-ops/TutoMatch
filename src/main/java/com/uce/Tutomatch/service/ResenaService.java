package com.uce.Tutomatch.service;

import com.uce.Tutomatch.model.PerfilTutor;
import com.uce.Tutomatch.model.Resena;
import com.uce.Tutomatch.model.Reserva;
import com.uce.Tutomatch.repository.PerfilTutorRepository;
import com.uce.Tutomatch.repository.ResenaRepository;
import com.uce.Tutomatch.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class ResenaService {

    private final ResenaRepository resenaRepository;
    private final ReservaRepository reservaRepository;
    private final PerfilTutorRepository perfilTutorRepository;

    public ResenaService(ResenaRepository resenaRepository,
                         ReservaRepository reservaRepository,
                         PerfilTutorRepository perfilTutorRepository) {
        this.resenaRepository = resenaRepository;
        this.reservaRepository = reservaRepository;
        this.perfilTutorRepository = perfilTutorRepository;
    }

    @Transactional
    public Resena crear(Long reservaId, Long solicitanteId, Integer calificacion, String comentario) {
        if (calificacion < 1 || calificacion > 5) {
            throw new IllegalArgumentException("La calificación debe estar entre 1 y 5");
        }

        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        if (!reserva.getSolicitante().getId().equals(solicitanteId)) {
            throw new IllegalArgumentException("No puedes calificar una tutoría que no te pertenece");
        }

        if (reserva.getEstado() != Reserva.EstadoReserva.FINALIZADA) {
            throw new IllegalArgumentException("Solo puedes calificar tutorías finalizadas");
        }

        if (resenaRepository.findByReservaId(reservaId).isPresent()) {
            throw new IllegalArgumentException("Ya calificaste esta tutoría");
        }

        Resena resena = new Resena(reserva, calificacion, comentario != null ? comentario : "");
        resena = resenaRepository.save(resena);

        actualizarPromedioTutor(reserva.getDisponibilidad().getPerfilTutor().getId());

        return resena;
    }

    @Transactional
    public void eliminar(Long resenaId, boolean esAdmin) {
        if (!esAdmin) {
            throw new IllegalArgumentException("Solo administradores pueden eliminar reseñas");
        }
        Resena resena = resenaRepository.findById(resenaId)
                .orElseThrow(() -> new IllegalArgumentException("Reseña no encontrada"));
        Long perfilTutorId = resena.getReserva().getDisponibilidad().getPerfilTutor().getId();
        resenaRepository.delete(resena);
        actualizarPromedioTutor(perfilTutorId);
    }

    private void actualizarPromedioTutor(Long perfilTutorId) {
        PerfilTutor tutor = perfilTutorRepository.findById(perfilTutorId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de tutor no encontrado"));
        Double promedio = resenaRepository.promedioCalificacionPorTutor(perfilTutorId);
        tutor.setCalificacionPromedio(promedio != null
                ? BigDecimal.valueOf(promedio).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO);
        perfilTutorRepository.save(tutor);
    }

    public List<Resena> obtenerPorTutor(Long perfilTutorId) {
        return resenaRepository.findByReservaDisponibilidadPerfilTutorIdOrderByFechaDesc(perfilTutorId);
    }

    public boolean existeResena(Long reservaId) {
        return resenaRepository.findByReservaId(reservaId).isPresent();
    }
}
