package com.uce.Tutomatch.service;

import com.uce.Tutomatch.model.Disponibilidad;
import com.uce.Tutomatch.model.Notificacion;
import com.uce.Tutomatch.model.Reserva;
import com.uce.Tutomatch.model.TransaccionToken.TipoTransaccion;
import com.uce.Tutomatch.repository.ReservaRepository;
import com.uce.Tutomatch.repository.DisponibilidadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservaPagoService {

    private final WalletOperacionService walletOperaciones;
    private final ReservaRepository reservaRepository;
    private final DisponibilidadRepository disponibilidadRepository;
    private final NotificacionService notificacionService;

    public ReservaPagoService(WalletOperacionService walletOperaciones,
                               ReservaRepository reservaRepository,
                               DisponibilidadRepository disponibilidadRepository,
                               NotificacionService notificacionService) {
        this.walletOperaciones = walletOperaciones;
        this.reservaRepository = reservaRepository;
        this.disponibilidadRepository = disponibilidadRepository;
        this.notificacionService = notificacionService;
    }

    @Transactional
    public Reserva marcarSesionImpartida(Long reservaId, Long tutorUsuarioId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        if (reserva.getEstado() != Reserva.EstadoReserva.CONFIRMADA) {
            throw new IllegalArgumentException("Solo se pueden marcar como impartidas reservas confirmadas");
        }

        Long tutorId = reserva.getDisponibilidad().getPerfilTutor().getUsuario().getId();
        if (!tutorId.equals(tutorUsuarioId)) {
            throw new IllegalArgumentException("Solo el tutor puede marcar la sesión como impartida");
        }

        reserva.setEstado(Reserva.EstadoReserva.PENDIENTE_PAGO);
        reserva = reservaRepository.save(reserva);

        notificacionService.crear(reserva.getSolicitante(),
                Notificacion.TipoNotificacion.RESERVA_CONFIRMADA,
                "El tutor marcó la tutoría de " + reserva.getMateria().getNombre()
                        + " como impartida. Ahora puedes pagar con "
                        + reserva.getCostoTokens() + " token(s) para finalizarla.");

        return reserva;
    }

    @Transactional
    public Reserva pagarConToken(Long reservaId, Long estudianteId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        if (reserva.getEstado() != Reserva.EstadoReserva.PENDIENTE_PAGO) {
            throw new IllegalArgumentException("Esta reserva no está pendiente de pago");
        }

        if (!reserva.getSolicitante().getId().equals(estudianteId)) {
            throw new IllegalArgumentException("Solo el estudiante puede pagar esta reserva");
        }

        int costo = reserva.getCostoTokens();
        Long tutorId = reserva.getDisponibilidad().getPerfilTutor().getUsuario().getId();

        walletOperaciones.debitar(estudianteId, costo,
                TipoTransaccion.PAGO_TUTORIA_RECIBIDA,
                "Pago de tutoría: " + reserva.getMateria().getNombre(), reservaId);

        reserva.setEstado(Reserva.EstadoReserva.FINALIZADA);
        reservaRepository.save(reserva);

        walletOperaciones.acreditar(tutorId, costo,
                TipoTransaccion.INGRESO_TUTORIA_DADA,
                "Ingreso por tutoría: " + reserva.getMateria().getNombre(), reservaId);

        notificacionService.crear(reserva.getSolicitante(),
                Notificacion.TipoNotificacion.PAGO_REALIZADO,
                "Pagaste " + costo + " token(s) por la tutoría de "
                        + reserva.getMateria().getNombre() + ". ¡Ya puedes calificarla!");

        notificacionService.crear(reserva.getDisponibilidad().getPerfilTutor().getUsuario(),
                Notificacion.TipoNotificacion.PAGO_RECIBIDO,
                "Recibiste " + costo + " token(s) por la tutoría de "
                        + reserva.getMateria().getNombre()
                        + " con " + reserva.getSolicitante().getNombreCompleto() + ".");

        return reserva;
    }

    @Transactional
    public Reserva cancelarEnPendientePago(Long reservaId, Long estudianteId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        if (reserva.getEstado() != Reserva.EstadoReserva.PENDIENTE_PAGO) {
            throw new IllegalArgumentException("Esta reserva no está pendiente de pago");
        }

        if (!reserva.getSolicitante().getId().equals(estudianteId)) {
            throw new IllegalArgumentException("Solo el estudiante puede cancelar en este estado");
        }

        reserva.setEstado(Reserva.EstadoReserva.CANCELADA);
        reservaRepository.save(reserva);

        Disponibilidad bloque = reserva.getDisponibilidad();
        bloque.setEstado(Disponibilidad.EstadoDisponibilidad.LIBRE);
        disponibilidadRepository.save(bloque);

        notificacionService.crear(reserva.getSolicitante(),
                Notificacion.TipoNotificacion.RESERVA_CANCELADA,
                "Cancelaste la tutoría de " + reserva.getMateria().getNombre()
                        + " (pendiente de pago). No se movieron tokens.");

        notificacionService.crear(reserva.getDisponibilidad().getPerfilTutor().getUsuario(),
                Notificacion.TipoNotificacion.RESERVA_CANCELADA,
                "El estudiante canceló la tutoría de " + reserva.getMateria().getNombre()
                        + " antes de pagar.");

        return reserva;
    }
}
