package com.uce.Tutomatch.controller;

import com.uce.Tutomatch.model.Resena;
import com.uce.Tutomatch.repository.ResenaRepository;
import com.uce.Tutomatch.util.AuthUtil;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminResenaController {

    private final ResenaRepository resenaRepository;
    private final AuthUtil authUtil;

    public AdminResenaController(ResenaRepository resenaRepository,
                                  AuthUtil authUtil) {
        this.resenaRepository = resenaRepository;
        this.authUtil = authUtil;
    }

    @GetMapping("/resenas")
    public String listarResenas(Authentication auth,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 Model model) {
        if (!authUtil.esAdmin(auth)) return "redirect:/auth/login?error=credenciales";

        Pageable pageable = PageRequest.of(page, size, Sort.by("fecha").descending());
        Page<Resena> resenasPage = resenaRepository.findAllByOrderByFechaDesc(pageable);

        model.addAttribute("resenas", resenasPage.getContent());
        model.addAttribute("page", resenasPage);
        model.addAttribute("authenticated", true);
        model.addAttribute("isAdmin", true);
        return "admin-resenas";
    }

    @PostMapping("/resenas/{id}/eliminar")
    public String eliminarResena(Authentication auth,
                                  @PathVariable Long id) {
        if (!authUtil.esAdmin(auth)) return "redirect:/auth/login?error=credenciales";

        try {
            resenaRepository.deleteById(id);
            return "redirect:/admin/resenas?success=resena_eliminada";
        } catch (Exception e) {
            return "redirect:/admin/resenas?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }
}
