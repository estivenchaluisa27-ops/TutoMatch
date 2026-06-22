package com.uce.Tutomatch.controller;

import com.uce.Tutomatch.dto.LoginRequest;
import com.uce.Tutomatch.dto.RegistroRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/auth/login")
    public String loginForm(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        model.addAttribute("authenticated", false);
        return "login";
    }

    @GetMapping("/auth/registro")
    public String registroForm(Model model) {
        model.addAttribute("registroRequest", new RegistroRequest());
        model.addAttribute("authenticated", false);
        return "registro";
    }
}
