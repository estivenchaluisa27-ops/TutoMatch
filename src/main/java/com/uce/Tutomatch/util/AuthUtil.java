package com.uce.Tutomatch.util;

import com.uce.Tutomatch.model.Usuario;
import com.uce.Tutomatch.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {

    private final UsuarioRepository usuarioRepository;

    public AuthUtil(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public static boolean estaAutenticado(Authentication auth) {
        return auth != null && auth.isAuthenticated();
    }

    public Long obtenerUsuarioId(Authentication auth) {
        String email = auth.getName();
        return usuarioRepository.findByCorreoInstitucional(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"))
                .getId();
    }

    public Usuario obtenerUsuario(Authentication auth) {
        String email = auth.getName();
        return usuarioRepository.findByCorreoInstitucional(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    public boolean esAdmin(Authentication auth) {
        return estaAutenticado(auth)
                && usuarioRepository.findByCorreoInstitucional(auth.getName())
                        .map(Usuario::isRolAdmin)
                        .orElse(false);
    }
}
