package com.uce.Tutomatch.controller;

import com.uce.Tutomatch.dto.GuardarResenaDTO;
import com.uce.Tutomatch.model.Usuario;
import com.uce.Tutomatch.service.ResenaService;
import com.uce.Tutomatch.util.AuthUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/resenas")
public class ResenaController {

    private static final Logger log = LoggerFactory.getLogger(ResenaController.class);

    private final ResenaService resenaService;
    private final AuthUtil authUtil;

    public ResenaController(ResenaService resenaService,
                            AuthUtil authUtil) {
        this.resenaService = resenaService;
        this.authUtil = authUtil;
    }

    @GetMapping("/nueva/{reservaId}")
    public String mostrarFormulario(@PathVariable Long reservaId,
                                    Authentication auth,
                                    Model model) {
        if (!AuthUtil.estaAutenticado(auth)) return "redirect:/auth/login";

        model.addAttribute("authenticated", true);
        model.addAttribute("reservaId", reservaId);
        return "formulario-resena";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute GuardarResenaDTO dto,
                          BindingResult result,
                          Authentication auth,
                          RedirectAttributes ra) {
        if (!AuthUtil.estaAutenticado(auth)) return "redirect:/auth/login";
        if (result.hasErrors()) {
            ra.addFlashAttribute("error", "Verifica los campos: calificación 1-5");
            return "redirect:/reservas";
        }

        try {
            Long usuarioId = authUtil.obtenerUsuarioId(auth);
            resenaService.crear(dto.getReservaId(), usuarioId, dto.getCalificacion(), dto.getComentario());
            ra.addFlashAttribute("success", "Calificación guardada correctamente");
        } catch (Exception e) {
            log.error("Error al guardar reseña: {}", e.getMessage(), e);
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/reservas";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id,
                           Authentication auth,
                           RedirectAttributes ra) {
        if (!AuthUtil.estaAutenticado(auth)) return "redirect:/auth/login";

        try {
            Usuario user = authUtil.obtenerUsuario(auth);
            boolean esAdmin = user.isRolAdmin();
            resenaService.eliminar(id, esAdmin);
            ra.addFlashAttribute("success", "Reseña eliminada correctamente");
        } catch (Exception e) {
            log.error("Error al eliminar reseña: {}", e.getMessage(), e);
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/reservas";
    }
}
