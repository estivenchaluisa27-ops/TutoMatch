package com.uce.Tutomatch.controller;

import com.uce.Tutomatch.model.Notificacion;
import com.uce.Tutomatch.repository.UsuarioRepository;
import com.uce.Tutomatch.service.NotificacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;
    private final UsuarioRepository usuarioRepository;

    public NotificacionController(NotificacionService notificacionService,
                                   UsuarioRepository usuarioRepository) {
        this.notificacionService = notificacionService;
        this.usuarioRepository = usuarioRepository;
    }

    private Long obtenerUsuarioId(Authentication auth) {
        String email = auth.getName();
        return usuarioRepository.findByCorreoInstitucional(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"))
                .getId();
    }

    @GetMapping
    public ResponseEntity<List<Notificacion>> listar(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        Long usuarioId = obtenerUsuarioId(auth);
        List<Notificacion> notificaciones = notificacionService.obtenerPorUsuario(usuarioId);
        return ResponseEntity.ok(notificaciones);
    }

    @GetMapping("/contar")
    public ResponseEntity<Map<String, Long>> contarNoLeidas(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.ok(Map.of("noLeidas", 0L));
        }
        Long usuarioId = obtenerUsuarioId(auth);
        long count = notificacionService.contarNoLeidas(usuarioId);
        return ResponseEntity.ok(Map.of("noLeidas", count));
    }

    @PostMapping("/{id}/leer")
    public ResponseEntity<Void> marcarLeida(Authentication auth, @PathVariable Long id) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        Long usuarioId = obtenerUsuarioId(auth);
        var notificacion = notificacionService.obtenerPorId(id);
        if (notificacion == null || !notificacion.getUsuario().getId().equals(usuarioId)) {
            return ResponseEntity.status(403).build();
        }
        notificacionService.marcarLeida(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/leer-todas")
    public ResponseEntity<Void> marcarTodasLeidas(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        Long usuarioId = obtenerUsuarioId(auth);
        notificacionService.marcarTodasLeidas(usuarioId);
        return ResponseEntity.ok().build();
    }
}
