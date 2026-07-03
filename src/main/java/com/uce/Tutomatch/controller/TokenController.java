package com.uce.Tutomatch.controller;

import com.uce.Tutomatch.repository.UsuarioRepository;
import com.uce.Tutomatch.service.WalletConsultaService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TokenController {

    private final WalletConsultaService walletConsulta;
    private final UsuarioRepository usuarioRepository;

    public TokenController(WalletConsultaService walletConsulta,
                           UsuarioRepository usuarioRepository) {
        this.walletConsulta = walletConsulta;
        this.usuarioRepository = usuarioRepository;
    }

    private Long obtenerUsuarioId(Authentication auth) {
        String email = auth.getName();
        return usuarioRepository.findByCorreoInstitucional(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"))
                .getId();
    }

    @GetMapping("/wallet")
    public String verWallet(Authentication auth, Model model, Pageable pageable) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        Long usuarioId = obtenerUsuarioId(auth);
        model.addAttribute("saldo", walletConsulta.obtenerSaldo(usuarioId));
        model.addAttribute("transacciones", walletConsulta.obtenerHistorial(usuarioId, pageable));
        model.addAttribute("authenticated", true);
        return "mi-wallet";
    }
}
