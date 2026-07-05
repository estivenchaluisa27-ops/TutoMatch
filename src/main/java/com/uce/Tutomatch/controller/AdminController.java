package com.uce.Tutomatch.controller;

import com.uce.Tutomatch.repository.PerfilTutorRepository;
import com.uce.Tutomatch.repository.UsuarioRepository;
import com.uce.Tutomatch.util.AuthUtil;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UsuarioRepository usuarioRepository;
    private final PerfilTutorRepository perfilTutorRepository;
    private final AuthUtil authUtil;

    public AdminController(UsuarioRepository usuarioRepository,
                           PerfilTutorRepository perfilTutorRepository,
                           AuthUtil authUtil) {
        this.usuarioRepository = usuarioRepository;
        this.perfilTutorRepository = perfilTutorRepository;
        this.authUtil = authUtil;
    }

    @GetMapping
    public String dashboard(Authentication auth, Model model) {
        if (!authUtil.esAdmin(auth)) return "redirect:/auth/login?error=credenciales";

        long totalUsuarios = usuarioRepository.count();
        long totalTutores = perfilTutorRepository.count();
        long tutoresVerificados = perfilTutorRepository.countByVerificadoTrue();
        long tutoresPendientes = perfilTutorRepository.countByVerificadoFalse();
        long reservasHoy = 0;
        long reservasPendientes = 0;
        long reservasActivas = 0;

        model.addAttribute("totalUsuarios", totalUsuarios);
        model.addAttribute("totalTutores", totalTutores);
        model.addAttribute("tutoresVerificados", tutoresVerificados);
        model.addAttribute("tutoresPendientes", tutoresPendientes);
        model.addAttribute("reservasHoy", reservasHoy);
        model.addAttribute("reservasPendientes", reservasPendientes);
        model.addAttribute("reservasActivas", reservasActivas);
        model.addAttribute("authenticated", true);
        model.addAttribute("isAdmin", true);
        return "admin-dashboard";
    }
}
