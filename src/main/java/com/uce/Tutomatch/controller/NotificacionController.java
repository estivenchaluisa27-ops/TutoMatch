package com.uce.Tutomatch.controller;

import com.uce.Tutomatch.model.Notificacion;
import com.uce.Tutomatch.service.NotificacionService;
import com.uce.Tutomatch.util.AuthUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;
    private final AuthUtil authUtil;

    public NotificacionController(NotificacionService notificacionService,
                                   AuthUtil authUtil) {
        this.notificacionService = notificacionService;
        this.authUtil = authUtil;
    }

    @GetMapping
    public ResponseEntity<List<Notificacion>> listar(Authentication auth) {
        if (!AuthUtil.estaAutenticado(auth)) {
            return ResponseEntity.status(401).build();
        }
        Long usuarioId = authUtil.obtenerUsuarioId(auth);
        List<Notificacion> notificaciones = notificacionService.obtenerPorUsuario(usuarioId);
        return ResponseEntity.ok(notificaciones);
    }

    @GetMapping("/contar")
    public ResponseEntity<Map<String, Long>> contarNoLeidas(Authentication auth) {
        if (!AuthUtil.estaAutenticado(auth)) {
            return ResponseEntity.ok(Map.of("noLeidas", 0L));
        }
        Long usuarioId = authUtil.obtenerUsuarioId(auth);
        long count = notificacionService.contarNoLeidas(usuarioId);
        return ResponseEntity.ok(Map.of("noLeidas", count));
    }

    @PostMapping("/{id}/leer")
    public ResponseEntity<Void> marcarLeida(Authentication auth, @PathVariable Long id) {
        if (!AuthUtil.estaAutenticado(auth)) {
            return ResponseEntity.status(401).build();
        }
        Long usuarioId = authUtil.obtenerUsuarioId(auth);
        var notificacion = notificacionService.obtenerPorId(id);
        if (notificacion == null || !notificacion.getUsuario().getId().equals(usuarioId)) {
            return ResponseEntity.status(403).build();
        }
        notificacionService.marcarLeida(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/leer-todas")
    public ResponseEntity<Void> marcarTodasLeidas(Authentication auth) {
        if (!AuthUtil.estaAutenticado(auth)) {
            return ResponseEntity.status(401).build();
        }
        Long usuarioId = authUtil.obtenerUsuarioId(auth);
        notificacionService.marcarTodasLeidas(usuarioId);
        return ResponseEntity.ok().build();
    }
}
