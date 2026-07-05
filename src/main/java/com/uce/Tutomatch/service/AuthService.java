package com.uce.Tutomatch.service;

import com.uce.Tutomatch.dto.LoginRequest;
import com.uce.Tutomatch.dto.UsuarioResponse;
import com.uce.Tutomatch.exception.InvalidCredentialsException;
import com.uce.Tutomatch.model.Usuario;
import com.uce.Tutomatch.repository.UsuarioRepository;
import com.uce.Tutomatch.security.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final boolean cookieSecure;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       @Value("${cookie.secure:true}") boolean cookieSecure) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.cookieSecure = cookieSecure;
    }

    public UsuarioResponse login(LoginRequest request, HttpServletResponse response) {
        Usuario usuario = usuarioRepository.findByCorreoInstitucional(request.getCorreoInstitucional())
                .orElseThrow(() -> new InvalidCredentialsException("Credenciales inv\u00e1lidas"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
            throw new InvalidCredentialsException("Credenciales inv\u00e1lidas");
        }

        setJwtCookie(response, usuario);
        return UsuarioResponse.from(usuario);
    }

    public void logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt-token", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);
    }

    public void setJwtCookie(HttpServletResponse response, Usuario usuario) {
        String token = jwtTokenProvider.generateToken(
                new org.springframework.security.core.userdetails.User(
                        usuario.getCorreoInstitucional(),
                        "",
                        new java.util.ArrayList<>()
                ),
                usuario.isRolTutor(),
                usuario.isRolAdmin()
        );

        Cookie cookie = new Cookie("jwt-token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/");
        cookie.setMaxAge((int) (1800000 / 1000));
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);
    }
}
