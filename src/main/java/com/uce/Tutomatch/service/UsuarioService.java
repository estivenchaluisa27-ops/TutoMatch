package com.uce.Tutomatch.service;

import com.uce.Tutomatch.dto.RegistroRequest;
import com.uce.Tutomatch.dto.LoginRequest;
import com.uce.Tutomatch.dto.UsuarioResponse;
import com.uce.Tutomatch.model.PerfilTutor;
import com.uce.Tutomatch.model.Usuario;
import com.uce.Tutomatch.repository.PerfilTutorRepository;
import com.uce.Tutomatch.repository.UsuarioRepository;
import com.uce.Tutomatch.exception.EmailAlreadyExistsException;
import com.uce.Tutomatch.exception.InvalidCredentialsException;
import com.uce.Tutomatch.security.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PerfilTutorRepository perfilTutorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final boolean cookieSecure;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PerfilTutorRepository perfilTutorRepository,
                          PasswordEncoder passwordEncoder,
                          JwtTokenProvider jwtTokenProvider,
                          @Value("${cookie.secure:true}") boolean cookieSecure) {
        this.usuarioRepository = usuarioRepository;
        this.perfilTutorRepository = perfilTutorRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.cookieSecure = cookieSecure;
    }

    @Transactional
    public UsuarioResponse registrar(RegistroRequest request, HttpServletResponse response) {
        if (usuarioRepository.existsByCorreoInstitucional(request.getCorreoInstitucional())) {
            throw new EmailAlreadyExistsException("El correo institucional ya está registrado");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        Usuario usuario = new Usuario(
                request.getCorreoInstitucional(),
                encodedPassword,
                request.getNombreCompleto(),
                request.isRolSolicitante(),
                request.isRolTutor(),
                false // rolAdmin siempre false en registro público
        );

        // Si se registra como tutor, crear perfil de tutor vacío
        if (request.isRolTutor()) {
            PerfilTutor perfil = new PerfilTutor();
            perfil.setUsuario(usuario);
            perfil.setSemestre(1); // valor por defecto
            perfil.setDescripcion("");
            perfil.setVerificado(false);
            perfil.setVisible(false); // no visible hasta verificación
            usuario.setPerfilTutor(perfil);
        }

        Usuario saved = usuarioRepository.save(usuario);
        setJwtCookie(response, saved);
        return UsuarioResponse.from(saved);
    }

    public UsuarioResponse login(LoginRequest request, HttpServletResponse response) {
        Usuario usuario = usuarioRepository.findByCorreoInstitucional(request.getCorreoInstitucional())
                .orElseThrow(() -> new InvalidCredentialsException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
            throw new InvalidCredentialsException("Credenciales inválidas");
        }

        setJwtCookie(response, usuario);
        return UsuarioResponse.from(usuario);
    }

    public void logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt-token", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/");
        cookie.setMaxAge(0); // expira inmediatamente
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);
    }

    public UsuarioResponse obtenerPerfil(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return UsuarioResponse.from(usuario);
    }

    public Usuario obtenerPorEmail(String email) {
        return usuarioRepository.findByCorreoInstitucional(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    private void setJwtCookie(HttpServletResponse response, Usuario usuario) {
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
        cookie.setMaxAge((int) (1800000 / 1000)); // 30 min en segundos
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);
    }
}
