package com.uce.Tutomatch.controller;

import com.uce.Tutomatch.dto.GuardarResenaDTO;
import com.uce.Tutomatch.model.Usuario;
import com.uce.Tutomatch.service.ResenaService;
import com.uce.Tutomatch.util.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/resenas")
public class ResenaController {

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

        Long usuarioId = authUtil.obtenerUsuarioId(auth);
        resenaService.crear(dto.getReservaId(), usuarioId, dto.getCalificacion(), dto.getComentario());
        ra.addFlashAttribute("success", "Calificación guardada correctamente");

        return "redirect:/reservas";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id,
                           Authentication auth,
                           RedirectAttributes ra) {
        if (!AuthUtil.estaAutenticado(auth)) return "redirect:/auth/login";

        Usuario user = authUtil.obtenerUsuario(auth);
        resenaService.eliminar(id, user.isRolAdmin());
        ra.addFlashAttribute("success", "Reseña eliminada correctamente");

        return "redirect:/reservas";
    }
}
