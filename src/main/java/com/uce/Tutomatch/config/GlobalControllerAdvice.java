package com.uce.Tutomatch.config;

import com.uce.Tutomatch.repository.UsuarioRepository;
import com.uce.Tutomatch.service.NotificacionService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final NotificacionService notificacionService;
    private final UsuarioRepository usuarioRepository;

    public GlobalControllerAdvice(NotificacionService notificacionService,
                                   UsuarioRepository usuarioRepository) {
        this.notificacionService = notificacionService;
        this.usuarioRepository = usuarioRepository;
    }

    @ModelAttribute("isAdmin")
    public boolean isAdmin(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return false;
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    @ModelAttribute("notificacionesNoLeidas")
    public long notificacionesNoLeidas(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return 0;
        return usuarioRepository.findByCorreoInstitucional(authentication.getName())
                .map(u -> notificacionService.contarNoLeidas(u.getId()))
                .orElse(0L);
    }
}
