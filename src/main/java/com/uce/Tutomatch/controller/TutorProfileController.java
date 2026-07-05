package com.uce.Tutomatch.controller;

import com.uce.Tutomatch.dto.AgregarBloqueDTO;
import com.uce.Tutomatch.dto.AgregarMateriaDTO;
import com.uce.Tutomatch.dto.DescripcionDTO;
import com.uce.Tutomatch.dto.SemestreDTO;
import com.uce.Tutomatch.model.Disponibilidad;
import com.uce.Tutomatch.model.Materia;
import com.uce.Tutomatch.model.PerfilTutor;
import com.uce.Tutomatch.model.TutorMateria;
import com.uce.Tutomatch.model.Usuario;
import com.uce.Tutomatch.repository.MateriaRepository;
import com.uce.Tutomatch.repository.PerfilTutorRepository;
import com.uce.Tutomatch.repository.UsuarioRepository;
import com.uce.Tutomatch.service.DisponibilidadService;
import com.uce.Tutomatch.service.PerfilTutorService;
import com.uce.Tutomatch.util.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/tutor")
public class TutorProfileController {

    private final PerfilTutorService perfilTutorService;
    private final DisponibilidadService disponibilidadService;
    private final MateriaRepository materiaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PerfilTutorRepository perfilTutorRepository;
    private final AuthUtil authUtil;

    public TutorProfileController(PerfilTutorService perfilTutorService,
                                  DisponibilidadService disponibilidadService,
                                  MateriaRepository materiaRepository,
                                  UsuarioRepository usuarioRepository,
                                  PerfilTutorRepository perfilTutorRepository,
                                  AuthUtil authUtil) {
        this.perfilTutorService = perfilTutorService;
        this.disponibilidadService = disponibilidadService;
        this.materiaRepository = materiaRepository;
        this.usuarioRepository = usuarioRepository;
        this.perfilTutorRepository = perfilTutorRepository;
        this.authUtil = authUtil;
    }

    private PerfilTutor obtenerOCrearPerfilTutor(Long usuarioId) {
        return perfilTutorRepository.findByUsuarioId(usuarioId).orElseGet(() -> {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
            if (!usuario.isRolTutor()) {
                throw new IllegalArgumentException("No tienes rol de tutor");
            }
            PerfilTutor nuevo = new PerfilTutor(usuario, 1, "");
            nuevo.setVerificado(false);
            nuevo.setVisible(false);
            return perfilTutorRepository.save(nuevo);
        });
    }

    @GetMapping("/mi-perfil")
    public String verMiPerfil(Authentication auth, Model model) {
        if (!AuthUtil.estaAutenticado(auth)) {
            return "redirect:/auth/login";
        }

        Usuario usuario = authUtil.obtenerUsuario(auth);

        model.addAttribute("usuario", usuario);
        model.addAttribute("authenticated", true);

        try {
            PerfilTutor perfil = perfilTutorService.obtenerPorUsuarioId(usuario.getId());
            model.addAttribute("perfilTutor", perfil);
        } catch (IllegalArgumentException e) {
            model.addAttribute("perfilTutor", null);
        }

        return "mi-perfil";
    }

    @GetMapping("/perfil")
    @Transactional
    public String verPerfil(Authentication auth, Model model) {
        if (!AuthUtil.estaAutenticado(auth)) {
            return "redirect:/auth/login";
        }

        try {
            Long usuarioId = authUtil.obtenerUsuarioId(auth);
            PerfilTutor perfil = obtenerOCrearPerfilTutor(usuarioId);
            List<TutorMateria> materias = perfil.getMaterias();
            List<Materia> todasMaterias = materiaRepository.findAllByOrderByCategoriaAscNombreAsc();

            Map<String, List<Materia>> materiasPorCategoria = todasMaterias.stream()
                    .collect(Collectors.groupingBy(
                            Materia::getCategoria,
                            LinkedHashMap::new,
                            Collectors.toList()
                    ));

            model.addAttribute("perfil", perfil);
            model.addAttribute("materias", materias);
            model.addAttribute("materiasPorCategoria", materiasPorCategoria);
            model.addAttribute("authenticated", true);
        } catch (IllegalArgumentException e) {
            return "redirect:/?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }

        return "perfil-tutor";
    }

    @PostMapping("/perfil/descripcion")
    public String actualizarDescripcion(Authentication auth,
                                         @Valid @ModelAttribute DescripcionDTO dto,
                                         BindingResult result) {
        if (result.hasErrors()) return "redirect:/tutor/perfil?error=descripcion_invalida";
        try {
            perfilTutorService.actualizarDescripcion(authUtil.obtenerUsuarioId(auth), dto.getDescripcion());
            return "redirect:/tutor/perfil?success=descripcion";
        } catch (Exception e) {
            return "redirect:/tutor/perfil?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @PostMapping("/perfil/semestre")
    public String actualizarSemestre(Authentication auth,
                                      @Valid @ModelAttribute SemestreDTO dto,
                                      BindingResult result) {
        if (result.hasErrors()) return "redirect:/tutor/perfil?error=semestre_invalido";
        try {
            perfilTutorService.actualizarSemestre(authUtil.obtenerUsuarioId(auth), dto.getSemestre());
            return "redirect:/tutor/perfil?success=semestre";
        } catch (Exception e) {
            return "redirect:/tutor/perfil?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @PostMapping("/perfil/materias")
    public String agregarMateria(Authentication auth,
                                  @Valid @ModelAttribute AgregarMateriaDTO dto,
                                  BindingResult result) {
        if (result.hasErrors()) return "redirect:/tutor/perfil?error=verifica_los_campos";
        try {
            perfilTutorService.agregarMateria(authUtil.obtenerUsuarioId(auth), dto.getMateriaId());
            return "redirect:/tutor/perfil?success=materia";
        } catch (Exception e) {
            return "redirect:/tutor/perfil?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @PostMapping("/perfil/materias/{materiaId}/eliminar")
    public String eliminarMateria(Authentication auth,
                                  @PathVariable Long materiaId) {
        try {
            perfilTutorService.eliminarMateria(authUtil.obtenerUsuarioId(auth), materiaId);
            return "redirect:/tutor/perfil?success=materia_eliminada";
        } catch (Exception e) {
            return "redirect:/tutor/perfil?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @GetMapping("/disponibilidad")
    public String verDisponibilidad() {
        return "redirect:/tutor/disponibilidad/semanal";
    }

    @PostMapping("/disponibilidad")
    public String agregarBloque(Authentication auth,
                                @Valid @ModelAttribute AgregarBloqueDTO dto,
                                BindingResult result) {
        if (result.hasErrors()) return "redirect:/tutor/disponibilidad?error=verifica_los_campos";
        try {
            disponibilidadService.agregarBloque(
                    authUtil.obtenerUsuarioId(auth),
                    dto.getDiaSemana(),
                    LocalTime.parse(dto.getHoraInicio()),
                    LocalTime.parse(dto.getHoraFin()));
            return "redirect:/tutor/disponibilidad?success=bloque";
        } catch (Exception e) {
            return "redirect:/tutor/disponibilidad?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @PostMapping("/disponibilidad/{id}/eliminar")
    public String eliminarBloque(Authentication auth,
                                 @PathVariable Long id) {
        try {
            disponibilidadService.eliminarBloque(authUtil.obtenerUsuarioId(auth), id);
            return "redirect:/tutor/disponibilidad?success=bloque_eliminado";
        } catch (Exception e) {
            return "redirect:/tutor/disponibilidad?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }
}
