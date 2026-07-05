package com.uce.Tutomatch.controller;

import com.uce.Tutomatch.model.PerfilTutor;
import com.uce.Tutomatch.repository.PerfilTutorRepository;
import com.uce.Tutomatch.service.PerfilTutorService;
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
public class AdminTutorController {

    private final PerfilTutorService perfilTutorService;
    private final PerfilTutorRepository perfilTutorRepository;
    private final AuthUtil authUtil;

    public AdminTutorController(PerfilTutorService perfilTutorService,
                                 PerfilTutorRepository perfilTutorRepository,
                                 AuthUtil authUtil) {
        this.perfilTutorService = perfilTutorService;
        this.perfilTutorRepository = perfilTutorRepository;
        this.authUtil = authUtil;
    }

    @GetMapping("/tutores")
    public String listarTutores(Authentication auth,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(required = false) String filtro,
                                 Model model) {
        if (!authUtil.esAdmin(auth)) return "redirect:/auth/login?error=credenciales";

        Pageable pageable = PageRequest.of(page, size, Sort.by("usuario.nombreCompleto").ascending());
        Page<PerfilTutor> tutoresPage;

        if (filtro != null && !filtro.isBlank()) {
            tutoresPage = perfilTutorService.buscarTutoresAdmin(filtro, null, null, null, pageable);
        } else {
            tutoresPage = perfilTutorService.listarTodos(pageable);
        }

        model.addAttribute("tutores", tutoresPage.getContent());
        model.addAttribute("page", tutoresPage);
        model.addAttribute("filtro", filtro);
        model.addAttribute("pendientes", perfilTutorRepository.countByVerificadoFalse());
        model.addAttribute("authenticated", true);
        model.addAttribute("isAdmin", true);
        return "admin-tutores";
    }

    @PostMapping("/tutores/{id}/verificar")
    public String verificarTutor(Authentication auth,
                                  @PathVariable Long id,
                                  @RequestParam boolean verificado) {
        if (!authUtil.esAdmin(auth)) return "redirect:/auth/login?error=credenciales";

        try {
            perfilTutorService.verificarTutor(id, verificado);
            String msg = verificado ? "verificado" : "revocado";
            return "redirect:/admin/tutores?success=" + msg;
        } catch (Exception e) {
            return "redirect:/admin/tutores?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }
}
