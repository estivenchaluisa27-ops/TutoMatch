package com.uce.Tutomatch.controller;

import com.uce.Tutomatch.dto.MateriaDTO;
import com.uce.Tutomatch.model.Materia;
import com.uce.Tutomatch.repository.MateriaRepository;
import com.uce.Tutomatch.util.AuthUtil;
import jakarta.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminMateriaController {

    private final MateriaRepository materiaRepository;
    private final AuthUtil authUtil;

    public AdminMateriaController(MateriaRepository materiaRepository,
                                   AuthUtil authUtil) {
        this.materiaRepository = materiaRepository;
        this.authUtil = authUtil;
    }

    @GetMapping("/materias")
    public String listarMaterias(Authentication auth,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  Model model) {
        if (!authUtil.esAdmin(auth)) return "redirect:/auth/login?error=credenciales";

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
        if (!authUtil.esAdmin(auth)) return "redirect:/auth/login?error=credenciales";
        if (result.hasErrors()) return "redirect:/admin/materias?error=verifica_los_campos";

        try {
            Materia materia = new Materia(dto.getNombre(), dto.getCategoria(),
                    dto.getSemestreReferencial(), dto.getDescripcion(), dto.getIcono(),
                    null, null, null, null);
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
        if (!authUtil.esAdmin(auth)) return "redirect:/auth/login?error=credenciales";

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
        if (!authUtil.esAdmin(auth)) return "redirect:/auth/login?error=credenciales";

        try {
            materiaRepository.deleteById(id);
            return "redirect:/admin/materias?success=materia_eliminada";
        } catch (Exception e) {
            return "redirect:/admin/materias?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }
}
