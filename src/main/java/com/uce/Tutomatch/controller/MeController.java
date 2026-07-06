package com.uce.Tutomatch.controller;

import com.uce.Tutomatch.util.AuthUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MeController {

    private final AuthUtil authUtil;

    public MeController(AuthUtil authUtil) {
        this.authUtil = authUtil;
    }

    @GetMapping("/api/me")
    public ResponseEntity<Map<String, Object>> me(Authentication auth) {
        if (!AuthUtil.estaAutenticado(auth)) {
            return ResponseEntity.ok(Map.of("autenticado", false));
        }
        var usuario = authUtil.obtenerUsuario(auth);
        return ResponseEntity.ok(Map.of(
                "autenticado", true,
                "id", usuario.getId(),
                "nombre", usuario.getNombreCompleto(),
                "correo", usuario.getCorreoInstitucional()
        ));
    }
}
