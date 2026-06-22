package com.uce.Tutomatch.security;

import com.uce.Tutomatch.model.Usuario;
import com.uce.Tutomatch.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String correoInstitucional) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreoInstitucional(correoInstitucional)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + correoInstitucional));

        List<String> roles = new ArrayList<>();
        if (usuario.isRolSolicitante()) {
            roles.add("SOLICITANTE");
        }
        if (usuario.isRolTutor()) {
            roles.add("TUTOR");
        }
        if (usuario.isRolAdmin()) {
            roles.add("ADMIN");
        }

        return User.builder()
                .username(usuario.getCorreoInstitucional())
                .password(usuario.getPasswordHash())
                .roles(roles.toArray(new String[0]))
                .build();
    }
}
