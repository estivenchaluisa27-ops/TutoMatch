package com.uce.Tutomatch.controller;

import com.uce.Tutomatch.dto.MateriaDTO;
import com.uce.Tutomatch.model.Materia;
import com.uce.Tutomatch.model.PerfilTutor;
import com.uce.Tutomatch.model.Resena;
import com.uce.Tutomatch.repository.MateriaRepository;
import com.uce.Tutomatch.repository.PerfilTutorRepository;
import com.uce.Tutomatch.repository.ResenaRepository;
import com.uce.Tutomatch.repository.UsuarioRepository;
import com.uce.Tutomatch.service.PerfilTutorService;
import jakarta.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final PerfilTutorService perfilTutorService;
    private final UsuarioRepository usuarioRepository;
    private final PerfilTutorRepository perfilTutorRepository;
    private final MateriaRepository materiaRepository;
    private final ResenaRepository resenaRepository;

    public AdminController(PerfilTutorService perfilTutorService,
                           UsuarioRepository usuarioRepository,
                           PerfilTutorRepository perfilTutorRepository,
                           MateriaRepository materiaRepository,
                           ResenaRepository resenaRepository) {
        this.perfilTutorService = perfilTutorService;
        this.usuarioRepository = usuarioRepository;
        this.perfilTutorRepository = perfilTutorRepository;
        this.materiaRepository = materiaRepository;
        this.resenaRepository = resenaRepository;
    }

    private boolean esAdmin(Authentication auth) {
        return auth != null && auth.isAuthenticated()
                && usuarioRepository.findByCorreoInstitucional(auth.getName())
                        .map(u -> u.isRolAdmin())
                        .orElse(false);
    }

    private boolean verificarAdmin(Authentication auth) {
        if (!esAdmin(auth)) {
            return false;
        }
        return true;
    }

    @GetMapping
    public String dashboard(Authentication auth, Model model) {
        if (!verificarAdmin(auth)) return "redirect:/auth/login?error=credenciales";

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

    @GetMapping("/tutores")
    public String listarTutores(Authentication auth,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(required = false) String filtro,
                                 Model model) {
        if (!verificarAdmin(auth)) return "redirect:/auth/login?error=credenciales";

        Pageable pageable = PageRequest.of(page, size, Sort.by("usuario.nombreCompleto").ascending());
        Page<PerfilTutor> tutoresPage;

        if (filtro != null && !filtro.isBlank()) {
            tutoresPage = perfilTutorService.buscarTutores(filtro, null, null, null, pageable);
        } else {
            tutoresPage = perfilTutorService.listarTodos(pageable);
        }

        model.addAttribute("tutores", tutoresPage.getContent());
        model.addAttribute("page", tutoresPage);
        model.addAttribute("filtro", filtro);
        model.addAttribute("authenticated", true);
        model.addAttribute("isAdmin", true);
        return "admin-tutores";
    }

    @PostMapping("/tutores/{id}/verificar")
    public String verificarTutor(Authentication auth,
                                  @PathVariable Long id,
                                  @RequestParam boolean verificado) {
        if (!verificarAdmin(auth)) return "redirect:/auth/login?error=credenciales";

        try {
            perfilTutorService.verificarTutor(id, verificado);
            String msg = verificado ? "verificado" : "revocado";
            return "redirect:/admin/tutores?success=" + msg;
        } catch (Exception e) {
            return "redirect:/admin/tutores?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @GetMapping("/materias")
    public String listarMaterias(Authentication auth,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  Model model) {
        if (!verificarAdmin(auth)) return "redirect:/auth/login?error=credenciales";

        Pageable pageable = PageRequest.of(page, size, Sort.by("categoria").ascending().and(Sort.by("nombre").ascending()));
        Page<Materia> materiasPage = materiaRepository.findAllByOrderByCategoriaAscNombreAsc(pageable);
        Map<String, List<Materia>> materiasPorCategoria = materiasPage.getContent().stream()
                .collect(Collectors.groupingBy(Materia::getCategoria, LinkedHashMap::new, Collectors.toList()));

        model.addAttribute("materiasPorCategoria", materiasPorCategoria);
        model.addAttribute("page", materiasPage);
        model.addAttribute("authenticated", true);
        model.addAttribute("isAdmin", true);
        return "admin-materias";
    }

    @PostMapping("/materias/agregar")
    public String agregarMateria(Authentication auth,
                                  @Valid @ModelAttribute MateriaDTO dto,
                                  BindingResult result) {
        if (!verificarAdmin(auth)) return "redirect:/auth/login?error=credenciales";
        if (result.hasErrors()) return "redirect:/admin/materias?error=verifica_los_campos";

        try {
            Materia materia = new Materia(dto.getNombre(), dto.getCategoria(),
                    dto.getSemestreReferencial(), dto.getDescripcion(), dto.getIcono());
            materiaRepository.save(materia);
            return "redirect:/admin/materias?success=materia_creada";
        } catch (Exception e) {
            return "redirect:/admin/materias?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @PostMapping("/materias/{id}/editar")
    public String editarMateria(Authentication auth,
                                 @PathVariable Long id,
                                 @RequestParam String nombre,
                                 @RequestParam String categoria,
                                 @RequestParam(required = false) Integer semestreReferencial,
                                 @RequestParam(required = false) String descripcion,
                                 @RequestParam(required = false) String icono) {
        if (!verificarAdmin(auth)) return "redirect:/auth/login?error=credenciales";

        try {
            Materia materia = materiaRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Materia no encontrada"));
            materia.setNombre(nombre);
            materia.setCategoria(categoria);
            materia.setSemestreReferencial(semestreReferencial);
            materia.setDescripcion(descripcion);
            materia.setIcono(icono);
            materiaRepository.save(materia);
            return "redirect:/admin/materias?success=materia_editada";
        } catch (Exception e) {
            return "redirect:/admin/materias?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @PostMapping("/materias/{id}/eliminar")
    public String eliminarMateria(Authentication auth,
                                   @PathVariable Long id) {
        if (!verificarAdmin(auth)) return "redirect:/auth/login?error=credenciales";

        try {
            materiaRepository.deleteById(id);
            return "redirect:/admin/materias?success=materia_eliminada";
        } catch (Exception e) {
            return "redirect:/admin/materias?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @GetMapping("/resenas")
    public String listarResenas(Authentication auth,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 Model model) {
        if (!verificarAdmin(auth)) return "redirect:/auth/login?error=credenciales";

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
        if (!verificarAdmin(auth)) return "redirect:/auth/login?error=credenciales";

        try {
            resenaRepository.deleteById(id);
            return "redirect:/admin/resenas?success=resena_eliminada";
        } catch (Exception e) {
            return "redirect:/admin/resenas?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }
}
