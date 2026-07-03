package com.uce.Tutomatch.controller;

import com.uce.Tutomatch.model.Disponibilidad;
import com.uce.Tutomatch.model.PerfilTutor;
import com.uce.Tutomatch.model.Resena;
import com.uce.Tutomatch.model.TutorMateria;
import com.uce.Tutomatch.repository.MateriaRepository;
import com.uce.Tutomatch.service.PerfilTutorService;
import com.uce.Tutomatch.service.ResenaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class SearchController {

    private static final Logger log = LoggerFactory.getLogger(SearchController.class);

    private final PerfilTutorService perfilTutorService;
    private final MateriaRepository materiaRepository;
    private final ResenaService resenaService;

    public SearchController(PerfilTutorService perfilTutorService,
                            MateriaRepository materiaRepository,
                            ResenaService resenaService) {
        this.perfilTutorService = perfilTutorService;
        this.materiaRepository = materiaRepository;
        this.resenaService = resenaService;
    }

    @GetMapping("/buscar")
    public String buscarTutores(@RequestParam(required = false) String materia,
                                @RequestParam(required = false) String categoria,
                                @RequestParam(required = false) String minCalificacion,
                                @RequestParam(required = false) String semestre,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                Authentication authentication,
                                Model model) {
        model.addAttribute("authenticated", authentication != null && authentication.isAuthenticated());

        String materiaFilter = (materia != null && !materia.isBlank()) ? materia : null;
        String categoriaFilter = (categoria != null && !categoria.isBlank()) ? categoria : null;
        BigDecimal calificacionFilter = null;
        if (minCalificacion != null && !minCalificacion.isBlank()) {
            try { calificacionFilter = new BigDecimal(minCalificacion); } catch (NumberFormatException ignored) {}
        }
        Integer semestreFilter = null;
        if (semestre != null && !semestre.isBlank()) {
            try { semestreFilter = Integer.parseInt(semestre); } catch (NumberFormatException ignored) {}
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<PerfilTutor> resultadosPage = perfilTutorService.buscarTutores(
                materiaFilter, categoriaFilter, calificacionFilter, semestreFilter, pageable);
        model.addAttribute("resultados", resultadosPage.getContent());
        model.addAttribute("page", resultadosPage);
        model.addAttribute("materia", materiaFilter);
        model.addAttribute("categoria", categoriaFilter);
        model.addAttribute("minCalificacion", calificacionFilter);
        model.addAttribute("semestre", semestreFilter);

        List<String> categorias = materiaRepository.findAllByOrderByCategoriaAscNombreAsc()
                .stream()
                .map(m -> m.getCategoria())
                .distinct()
                .collect(Collectors.toList());
        model.addAttribute("categorias", categorias);

        List<Integer> semestres = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        model.addAttribute("semestres", semestres);

        return "resultados";
    }

    @GetMapping("/tutor/{id}")
    @Transactional(readOnly = true)
    public String verPerfilPublico(@PathVariable Long id,
                                    Authentication authentication,
                                    Model model) {
        model.addAttribute("authenticated", authentication != null && authentication.isAuthenticated());

        try {
            PerfilTutor tutor = perfilTutorService.obtenerPorId(id);
            if (!tutor.isVerificado() || !tutor.isVisible()) {
                return "redirect:/?error=tutor_no_disponible";
            }

            List<String> dias = List.of("Lunes", "Martes", "Mi\u00e9rcoles", "Jueves", "Viernes", "S\u00e1bado", "Domingo");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            Map<Integer, List<Map<String, Object>>> bloquesPorDia = new LinkedHashMap<>();
            for (Disponibilidad d : tutor.getDisponibilidades()) {
                if (d.getEstado() == Disponibilidad.EstadoDisponibilidad.LIBRE) {
                    bloquesPorDia.computeIfAbsent(d.getDiaSemana(), k -> new java.util.ArrayList<>())
                            .add(Map.of(
                                    "id", d.getId(),
                                    "horaInicio", d.getHoraInicio().format(formatter),
                                    "horaFin", d.getHoraFin().format(formatter)
                            ));
                }
            }

            List<Map<String, Object>> bloquesLista = new java.util.ArrayList<>();
            for (Disponibilidad d : tutor.getDisponibilidades()) {
                if (d.getEstado() == Disponibilidad.EstadoDisponibilidad.LIBRE) {
                    bloquesLista.add(Map.of(
                            "id", d.getId(),
                            "diaSemana", d.getDiaSemana(),
                            "horaInicio", d.getHoraInicio().format(formatter),
                            "horaFin", d.getHoraFin().format(formatter)
                    ));
                }
            }

            boolean esMiPerfil = authentication != null && authentication.isAuthenticated()
                    && tutor.getUsuario().getCorreoInstitucional().equals(authentication.getName());

            List<Resena> resenas = resenaService.obtenerPorTutor(id);
            Double promedio = tutor.getCalificacionPromedio().doubleValue();
            long totalResenas = resenas.size();

            model.addAttribute("tutor", tutor);
            model.addAttribute("bloquesPorDia", bloquesPorDia);
            model.addAttribute("bloquesLista", bloquesLista);
            model.addAttribute("dias", dias);
            model.addAttribute("esMiPerfil", esMiPerfil);
            model.addAttribute("resenas", resenas);
            model.addAttribute("totalResenas", totalResenas);
            model.addAttribute("promedio", promedio);
        } catch (Exception e) {
            log.error("Error al cargar perfil público del tutor {}: {}", id, e.getMessage(), e);
            return "redirect:/?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }

        return "perfil-publico";
    }
}
