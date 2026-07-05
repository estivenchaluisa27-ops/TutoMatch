package com.uce.Tutomatch.service;

import com.uce.Tutomatch.dto.RegistroRequest;
import com.uce.Tutomatch.dto.UsuarioResponse;
import com.uce.Tutomatch.model.PerfilTutor;
import com.uce.Tutomatch.model.Usuario;
import com.uce.Tutomatch.repository.PerfilTutorRepository;
import com.uce.Tutomatch.repository.UsuarioRepository;
import com.uce.Tutomatch.exception.EmailAlreadyExistsException;
import com.uce.Tutomatch.service.WalletOperacionService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PerfilTutorRepository perfilTutorRepository;
    private final PasswordEncoder passwordEncoder;
    private final WalletOperacionService walletOperaciones;
    private final AuthService authService;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PerfilTutorRepository perfilTutorRepository,
                          PasswordEncoder passwordEncoder,
                          WalletOperacionService walletOperaciones,
                          AuthService authService) {
        this.usuarioRepository = usuarioRepository;
        this.perfilTutorRepository = perfilTutorRepository;
        this.passwordEncoder = passwordEncoder;
        this.walletOperaciones = walletOperaciones;
        this.authService = authService;
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
        walletOperaciones.inicializarWallet(saved.getId());
        authService.setJwtCookie(response, saved);
        return UsuarioResponse.from(saved);
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

}
