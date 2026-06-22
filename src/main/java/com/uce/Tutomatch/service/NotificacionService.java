package com.uce.Tutomatch.service;

import com.uce.Tutomatch.model.Notificacion;
import com.uce.Tutomatch.model.Usuario;
import com.uce.Tutomatch.repository.NotificacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;

    public NotificacionService(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    @Transactional
    public Notificacion crear(Usuario usuario, Notificacion.TipoNotificacion tipo, String mensaje) {
        Notificacion notificacion = new Notificacion(usuario, tipo, mensaje);
        return notificacionRepository.save(notificacion);
    }

    @Transactional(readOnly = true)
    public List<Notificacion> obtenerPorUsuario(Long usuarioId) {
        return notificacionRepository.findByUsuarioIdOrderByFechaDesc(usuarioId);
    }

    @Transactional(readOnly = true)
    public long contarNoLeidas(Long usuarioId) {
        return notificacionRepository.countByUsuarioIdAndLeidaFalse(usuarioId);
    }

    @Transactional
    public void marcarLeida(Long notificacionId) {
        notificacionRepository.findById(notificacionId)
                .ifPresent(n -> {
                    n.setLeida(true);
                    notificacionRepository.save(n);
                });
    }

    @Transactional
    public void marcarTodasLeidas(Long usuarioId) {
        notificacionRepository.marcarTodasLeidasPorUsuario(usuarioId);
    }
}
