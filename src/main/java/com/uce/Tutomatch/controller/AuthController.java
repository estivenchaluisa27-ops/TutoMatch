package com.uce.Tutomatch.controller;

import com.uce.Tutomatch.dto.LoginRequest;
import com.uce.Tutomatch.dto.RegistroRequest;
import com.uce.Tutomatch.dto.UsuarioResponse;
import com.uce.Tutomatch.model.Usuario;
import com.uce.Tutomatch.service.AuthService;
import com.uce.Tutomatch.service.UsuarioService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final AuthService authService;

    public AuthController(UsuarioService usuarioService,
                          AuthService authService) {
        this.usuarioService = usuarioService;
        this.authService = authService;
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegistroRequest request, HttpServletResponse response) {
        UsuarioResponse usuario = usuarioService.registrar(request, response);
        return ResponseEntity.ok(usuario);
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        UsuarioResponse usuario = authService.login(request, response);
        return ResponseEntity.ok(usuario);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        authService.logout(response);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/perfil")
    public ResponseEntity<UsuarioResponse> perfil(org.springframework.security.core.Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioService.obtenerPorEmail(email);
        return ResponseEntity.ok(UsuarioResponse.from(usuario));
    }
}
