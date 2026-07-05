package com.uce.Tutomatch.config;

import com.uce.Tutomatch.repository.UsuarioRepository;
import com.uce.Tutomatch.service.NotificacionService;
import com.uce.Tutomatch.service.WalletConsultaService;
import com.uce.Tutomatch.util.AuthUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(GlobalControllerAdvice.class);

    private final NotificacionService notificacionService;
    private final UsuarioRepository usuarioRepository;
    private final WalletConsultaService walletConsulta;

    public GlobalControllerAdvice(NotificacionService notificacionService,
                                   UsuarioRepository usuarioRepository,
                                   WalletConsultaService walletConsulta) {
        this.notificacionService = notificacionService;
        this.usuarioRepository = usuarioRepository;
        this.walletConsulta = walletConsulta;
    }

    @ModelAttribute("authenticated")
    public boolean authenticated(Authentication authentication) {
        return AuthUtil.estaAutenticado(authentication);
    }

    @ModelAttribute("isAdmin")
    public boolean isAdmin(Authentication authentication) {
        if (!AuthUtil.estaAutenticado(authentication)) return false;
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    @ModelAttribute("notificacionesNoLeidas")
    public long notificacionesNoLeidas(Authentication authentication) {
        if (!AuthUtil.estaAutenticado(authentication)) return 0;
        return usuarioRepository.findByCorreoInstitucional(authentication.getName())
                .map(u -> notificacionService.contarNoLeidas(u.getId()))
                .orElse(0L);
    }

    @ModelAttribute("saldoTokens")
    public int saldoTokens(Authentication authentication) {
        if (!AuthUtil.estaAutenticado(authentication)) return 0;
        try {
            return usuarioRepository.findByCorreoInstitucional(authentication.getName())
                    .map(u -> walletConsulta.obtenerSaldo(u.getId()))
                    .orElse(0);
        } catch (Exception e) {
            log.warn("Error al obtener saldo de tokens: {}", e.getMessage());
            return 0;
        }
    }
}
