package com.uce.Tutomatch.controller;

import com.uce.Tutomatch.dto.GuardarResenaDTO;
import com.uce.Tutomatch.model.PerfilTutor;
import com.uce.Tutomatch.model.Resena;
import com.uce.Tutomatch.model.Usuario;
import com.uce.Tutomatch.repository.PerfilTutorRepository;
import com.uce.Tutomatch.repository.UsuarioRepository;
import com.uce.Tutomatch.service.ResenaService;
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
    private final UsuarioRepository usuarioRepository;
    private final PerfilTutorRepository perfilTutorRepository;

    public ResenaController(ResenaService resenaService,
                            UsuarioRepository usuarioRepository,
                            PerfilTutorRepository perfilTutorRepository) {
        this.resenaService = resenaService;
        this.usuarioRepository = usuarioRepository;
        this.perfilTutorRepository = perfilTutorRepository;
    }

    @GetMapping("/nueva/{reservaId}")
    public String mostrarFormulario(@PathVariable Long reservaId,
                                    Authentication auth,
                                    Model model) {
        if (auth == null) return "redirect:/auth/login";

        model.addAttribute("authenticated", true);
        model.addAttribute("reservaId", reservaId);
        return "formulario-resena";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute GuardarResenaDTO dto,
                          BindingResult result,
                          Authentication auth,
                          RedirectAttributes ra) {
        if (auth == null) return "redirect:/auth/login";
        if (result.hasErrors()) {
            ra.addFlashAttribute("error", "Verifica los campos: calificación 1-5");
            return "redirect:/reservas";
        }

        try {
            Usuario user = usuarioRepository.findByCorreoInstitucional(auth.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
            resenaService.crear(dto.getReservaId(), user.getId(), dto.getCalificacion(), dto.getComentario());
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
        if (auth == null) return "redirect:/auth/login";

        try {
            Usuario user = usuarioRepository.findByCorreoInstitucional(auth.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
            boolean esAdmin = "admin@uce.edu.ec".equals(user.getCorreoInstitucional());
            resenaService.eliminar(id, esAdmin);
            ra.addFlashAttribute("success", "Reseña eliminada correctamente");
        } catch (Exception e) {
            log.error("Error al eliminar reseña: {}", e.getMessage(), e);
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/reservas";
    }
}
