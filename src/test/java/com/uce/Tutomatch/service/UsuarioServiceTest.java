package com.uce.Tutomatch.service;

import com.uce.Tutomatch.dto.LoginRequest;
import com.uce.Tutomatch.dto.RegistroRequest;
import com.uce.Tutomatch.dto.UsuarioResponse;
import com.uce.Tutomatch.exception.EmailAlreadyExistsException;
import com.uce.Tutomatch.exception.InvalidCredentialsException;
import com.uce.Tutomatch.model.PerfilTutor;
import com.uce.Tutomatch.model.Usuario;
import com.uce.Tutomatch.repository.PerfilTutorRepository;
import com.uce.Tutomatch.repository.UsuarioRepository;
import com.uce.Tutomatch.security.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PerfilTutorRepository perfilTutorRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private HttpServletResponse response;

    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioService(usuarioRepository, perfilTutorRepository,
                passwordEncoder, jwtTokenProvider);
    }

    @Test
    void registrar_exito() {
        RegistroRequest request = new RegistroRequest();
        request.setCorreoInstitucional("test@uce.edu.ec");
        request.setPassword("password123");
        request.setNombreCompleto("Test User");
        request.setRolSolicitante(true);
        request.setRolTutor(false);

        when(usuarioRepository.existsByCorreoInstitucional("test@uce.edu.ec")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-pass");
        when(jwtTokenProvider.generateToken(any(), eq(false), eq(false))).thenReturn("mock-jwt");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> {
            Usuario u = i.getArgument(0);
            if (u.getId() == null) u.setId(1L);
            return u;
        });

        UsuarioResponse result = usuarioService.registrar(request, response);

        assertNotNull(result);
        assertEquals("test@uce.edu.ec", result.getCorreoInstitucional());
        assertEquals("Test User", result.getNombreCompleto());
        assertTrue(result.isRolSolicitante());
        assertFalse(result.isRolTutor());
        verify(response).addCookie(any(Cookie.class));
    }

    @Test
    void registrar_emailDuplicado_lanzaError() {
        RegistroRequest request = new RegistroRequest();
        request.setCorreoInstitucional("test@uce.edu.ec");
        request.setPassword("password123");
        request.setNombreCompleto("Test User");

        when(usuarioRepository.existsByCorreoInstitucional("test@uce.edu.ec")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> usuarioService.registrar(request, response));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void registrar_conRolTutor_creaPerfil() {
        RegistroRequest request = new RegistroRequest();
        request.setCorreoInstitucional("tutor@uce.edu.ec");
        request.setPassword("pass123");
        request.setNombreCompleto("Tutor User");
        request.setRolSolicitante(false);
        request.setRolTutor(true);

        when(usuarioRepository.existsByCorreoInstitucional("tutor@uce.edu.ec")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("encoded");
        when(jwtTokenProvider.generateToken(any(), eq(true), eq(false))).thenReturn("mock-jwt");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> {
            Usuario u = i.getArgument(0);
            if (u.getId() == null) u.setId(2L);
            return u;
        });

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        usuarioService.registrar(request, response);
        verify(usuarioRepository).save(captor.capture());

        Usuario saved = captor.getValue();
        assertTrue(saved.isRolTutor());
        assertNotNull(saved.getPerfilTutor());
        assertEquals(Integer.valueOf(1), saved.getPerfilTutor().getSemestre());
        assertFalse(saved.getPerfilTutor().isVerificado());
        assertFalse(saved.getPerfilTutor().isVisible());
    }

    @Test
    void login_exito() {
        LoginRequest request = new LoginRequest();
        request.setCorreoInstitucional("test@uce.edu.ec");
        request.setPassword("password123");

        Usuario usuario = new Usuario("test@uce.edu.ec", "encoded-pass", "Test User", true, false, false);
        usuario.setId(1L);

        when(usuarioRepository.findByCorreoInstitucional("test@uce.edu.ec"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("password123", "encoded-pass")).thenReturn(true);
        when(jwtTokenProvider.generateToken(any(), eq(false), eq(false))).thenReturn("mock-jwt");

        UsuarioResponse result = usuarioService.login(request, response);

        assertNotNull(result);
        assertEquals("test@uce.edu.ec", result.getCorreoInstitucional());
        verify(response).addCookie(any(Cookie.class));
    }

    @Test
    void login_credencialesInvalidas_lanzaError() {
        LoginRequest request = new LoginRequest();
        request.setCorreoInstitucional("test@uce.edu.ec");
        request.setPassword("wrong");

        when(usuarioRepository.findByCorreoInstitucional("test@uce.edu.ec"))
                .thenReturn(Optional.of(new Usuario("test@uce.edu.ec", "encoded", "T", true, false, false)));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> usuarioService.login(request, response));
    }

    @Test
    void logout_limpiaCookie() {
        usuarioService.logout(response);

        ArgumentCaptor<Cookie> captor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(captor.capture());

        Cookie cookie = captor.getValue();
        assertEquals("jwt-token", cookie.getName());
        assertEquals("", cookie.getValue());
        assertEquals(0, cookie.getMaxAge());
    }

    @Test
    void obtenerPerfil_exito() {
        Usuario usuario = new Usuario("test@uce.edu.ec", "pass", "Test", true, false, false);
        usuario.setId(1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        UsuarioResponse result = usuarioService.obtenerPerfil(1L);

        assertEquals("test@uce.edu.ec", result.getCorreoInstitucional());
        assertEquals("Test", result.getNombreCompleto());
    }

    @Test
    void obtenerPorEmail_exito() {
        Usuario usuario = new Usuario("test@uce.edu.ec", "pass", "Test", true, false, false);
        when(usuarioRepository.findByCorreoInstitucional("test@uce.edu.ec"))
                .thenReturn(Optional.of(usuario));

        Usuario result = usuarioService.obtenerPorEmail("test@uce.edu.ec");
        assertEquals("test@uce.edu.ec", result.getCorreoInstitucional());
    }
}
