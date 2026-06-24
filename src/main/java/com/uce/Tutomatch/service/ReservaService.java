package com.uce.Tutomatch.service;

import com.uce.Tutomatch.model.*;
import com.uce.Tutomatch.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final DisponibilidadRepository disponibilidadRepository;
    private final MateriaRepository materiaRepository;
    private final PerfilTutorRepository perfilTutorRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionService notificacionService;

    public ReservaService(ReservaRepository reservaRepository,
                          DisponibilidadRepository disponibilidadRepository,
                          MateriaRepository materiaRepository,
                          PerfilTutorRepository perfilTutorRepository,
                          UsuarioRepository usuarioRepository,
                          NotificacionService notificacionService) {
        this.reservaRepository = reservaRepository;
        this.disponibilidadRepository = disponibilidadRepository;
        this.materiaRepository = materiaRepository;
        this.perfilTutorRepository = perfilTutorRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificacionService = notificacionService;
    }

    private String diaSemanaNombre(Integer dia) {
        return List.of("Lunes", "Martes", "Mi\u00e9rcoles", "Jueves", "Viernes", "S\u00e1bado", "Domingo").get(dia - 1);
    }

    @Transactional
    public Reserva crear(Long solicitanteId, Long disponibilidadId, Long materiaId) {
        Disponibilidad bloque = disponibilidadRepository.findById(disponibilidadId)
                .orElseThrow(() -> new IllegalArgumentException("Bloque no encontrado"));

        if (bloque.getEstado() != Disponibilidad.EstadoDisponibilidad.LIBRE) {
            throw new IllegalArgumentException("El bloque no está disponible");
        }

        if (reservaRepository.existsByDisponibilidadIdAndEstadoNot(disponibilidadId, Reserva.EstadoReserva.CANCELADA)) {
            throw new IllegalArgumentException("El bloque ya tiene una reserva activa");
        }

        PerfilTutor perfilTutor = bloque.getPerfilTutor();
        if (!perfilTutor.isVerificado() || !perfilTutor.isVisible()) {
            throw new IllegalArgumentException("El tutor no está disponible actualmente");
        }

        Materia materia = materiaRepository.findById(materiaId)
                .orElseThrow(() -> new IllegalArgumentException("Materia no encontrada"));

        boolean tutorTieneMateria = perfilTutor.getMaterias().stream()
                .anyMatch(tm -> tm.getMateria().getId().equals(materiaId));
        if (!tutorTieneMateria) {
            throw new IllegalArgumentException("El tutor no ofrece esa materia");
        }

        Usuario solicitante = usuarioRepository.findById(solicitanteId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitante no encontrado"));

        if (perfilTutor.getUsuario().getId().equals(solicitanteId)) {
            throw new IllegalArgumentException("No puedes reservarte a ti mismo");
        }

        bloque.setEstado(Disponibilidad.EstadoDisponibilidad.RESERVADO);
        disponibilidadRepository.save(bloque);

        Reserva reserva = new Reserva(solicitante, bloque, materia);
        reserva = reservaRepository.save(reserva);

        notificacionService.crear(solicitante,
                Notificacion.TipoNotificacion.RESERVA_CREADA,
                "Solicitaste una tutor\u00eda de " + materia.getNombre()
                        + " para el " + diaSemanaNombre(bloque.getDiaSemana())
                        + " " + bloque.getHoraInicio() + "-" + bloque.getHoraFin());

        notificacionService.crear(perfilTutor.getUsuario(),
                Notificacion.TipoNotificacion.RESERVA_CREADA,
                solicitante.getNombreCompleto() + " solicit\u00f3 una tutor\u00eda de "
                        + materia.getNombre()
                        + " para el " + diaSemanaNombre(bloque.getDiaSemana())
                        + " " + bloque.getHoraInicio() + "-" + bloque.getHoraFin());

        return reserva;
    }

    @Transactional
    public Reserva confirmar(Long reservaId, Long usuarioId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        if (reserva.getEstado() != Reserva.EstadoReserva.PENDIENTE) {
            throw new IllegalArgumentException("Solo se pueden confirmar reservas pendientes");
        }

        Long tutorId = reserva.getDisponibilidad().getPerfilTutor().getUsuario().getId();
        if (!tutorId.equals(usuarioId)) {
            throw new IllegalArgumentException("Solo el tutor puede confirmar la reserva");
        }

        reserva.setEstado(Reserva.EstadoReserva.CONFIRMADA);
        reserva = reservaRepository.save(reserva);

        notificacionService.crear(reserva.getSolicitante(),
                Notificacion.TipoNotificacion.RESERVA_CONFIRMADA,
                "Tu tutor\u00eda de " + reserva.getMateria().getNombre()
                        + " con " + reserva.getDisponibilidad().getPerfilTutor().getUsuario().getNombreCompleto()
                        + " ha sido confirmada para el "
                        + diaSemanaNombre(reserva.getDisponibilidad().getDiaSemana())
                        + " " + reserva.getDisponibilidad().getHoraInicio() + "-" + reserva.getDisponibilidad().getHoraFin());

        return reserva;
    }

    @Transactional
    public Reserva finalizar(Long reservaId, Long usuarioId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        if (reserva.getEstado() != Reserva.EstadoReserva.CONFIRMADA) {
            throw new IllegalArgumentException("Solo se pueden finalizar reservas confirmadas");
        }

        Long tutorId = reserva.getDisponibilidad().getPerfilTutor().getUsuario().getId();
        if (!tutorId.equals(usuarioId)) {
            throw new IllegalArgumentException("Solo el tutor puede finalizar la reserva");
        }

        reserva.setEstado(Reserva.EstadoReserva.FINALIZADA);
        reserva = reservaRepository.save(reserva);

        notificacionService.crear(reserva.getSolicitante(),
                Notificacion.TipoNotificacion.RESERVA_FINALIZADA,
                "Tu tutor\u00eda de " + reserva.getMateria().getNombre()
                        + " con " + reserva.getDisponibilidad().getPerfilTutor().getUsuario().getNombreCompleto()
                        + " ha finalizado.");

        notificacionService.crear(reserva.getDisponibilidad().getPerfilTutor().getUsuario(),
                Notificacion.TipoNotificacion.RESERVA_FINALIZADA,
                "La tutor\u00eda de " + reserva.getMateria().getNombre()
                        + " con " + reserva.getSolicitante().getNombreCompleto()
                        + " ha finalizado.");

        return reserva;
    }

    @Transactional
    public Reserva cancelar(Long reservaId, Long usuarioId, boolean esAdmin) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        Reserva.EstadoReserva estado = reserva.getEstado();

        if (estado == Reserva.EstadoReserva.FINALIZADA || estado == Reserva.EstadoReserva.CANCELADA) {
            throw new IllegalArgumentException("La reserva ya está " + estado.name().toLowerCase());
        }

        Long tutorId = reserva.getDisponibilidad().getPerfilTutor().getUsuario().getId();
        Long solicitanteId = reserva.getSolicitante().getId();

        boolean esSolicitante = solicitanteId.equals(usuarioId);
        boolean esTutor = tutorId.equals(usuarioId);

        if (!esAdmin && !esSolicitante && !esTutor) {
            throw new IllegalArgumentException("No tienes permiso para cancelar esta reserva");
        }

        if (esTutor && estado != Reserva.EstadoReserva.PENDIENTE) {
            throw new IllegalArgumentException("El tutor solo puede cancelar reservas pendientes");
        }

        reserva.setEstado(Reserva.EstadoReserva.CANCELADA);
        reservaRepository.save(reserva);

        Disponibilidad bloque = reserva.getDisponibilidad();
        bloque.setEstado(Disponibilidad.EstadoDisponibilidad.LIBRE);
        disponibilidadRepository.save(bloque);

        notificacionService.crear(reserva.getSolicitante(),
                Notificacion.TipoNotificacion.RESERVA_CANCELADA,
                "Tu tutor\u00eda de " + reserva.getMateria().getNombre() + " fue cancelada.");

        notificacionService.crear(reserva.getDisponibilidad().getPerfilTutor().getUsuario(),
                Notificacion.TipoNotificacion.RESERVA_CANCELADA,
                "La tutor\u00eda de " + reserva.getMateria().getNombre()
                        + " con " + reserva.getSolicitante().getNombreCompleto() + " fue cancelada.");

        return reserva;
    }

    @Transactional(readOnly = true)
    public List<Reserva> obtenerComoSolicitante(Long usuarioId) {
        return reservaRepository.findBySolicitanteIdOrderByFechaCreacionDesc(usuarioId);
    }

    @Transactional(readOnly = true)
    public Page<Reserva> obtenerComoSolicitante(Long usuarioId, Pageable pageable) {
        return reservaRepository.findBySolicitanteIdOrderByFechaCreacionDesc(usuarioId, pageable);
    }

    @Transactional(readOnly = true)
    public List<Reserva> obtenerComoTutor(Long usuarioId) {
        PerfilTutor perfil = perfilTutorRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de tutor no encontrado"));
        return reservaRepository.findByDisponibilidadPerfilTutorIdOrderByFechaCreacionDesc(perfil.getId());
    }

    @Transactional(readOnly = true)
    public Page<Reserva> obtenerComoTutor(Long usuarioId, Pageable pageable) {
        PerfilTutor perfil = perfilTutorRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de tutor no encontrado"));
        return reservaRepository.findByDisponibilidadPerfilTutorIdOrderByFechaCreacionDesc(perfil.getId(), pageable);
    }
}
