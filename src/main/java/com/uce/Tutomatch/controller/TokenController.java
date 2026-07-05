package com.uce.Tutomatch.controller;

import com.uce.Tutomatch.service.WalletConsultaService;
import com.uce.Tutomatch.util.AuthUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TokenController {

    private final WalletConsultaService walletConsulta;
    private final AuthUtil authUtil;

    public TokenController(WalletConsultaService walletConsulta,
                           AuthUtil authUtil) {
        this.walletConsulta = walletConsulta;
        this.authUtil = authUtil;
    }

    @GetMapping("/wallet")
    public String verWallet(Authentication auth, Model model, Pageable pageable) {
        if (!AuthUtil.estaAutenticado(auth)) {
            return "redirect:/auth/login";
        }
        Long usuarioId = authUtil.obtenerUsuarioId(auth);
        model.addAttribute("saldo", walletConsulta.obtenerSaldo(usuarioId));
        model.addAttribute("transacciones", walletConsulta.obtenerHistorial(usuarioId, pageable));
        model.addAttribute("authenticated", true);
        return "mi-wallet";
    }
}
