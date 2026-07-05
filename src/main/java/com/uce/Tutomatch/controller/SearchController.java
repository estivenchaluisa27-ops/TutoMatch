package com.uce.Tutomatch.controller;

import com.uce.Tutomatch.model.Disponibilidad;
import com.uce.Tutomatch.model.Materia;
import com.uce.Tutomatch.model.PerfilTutor;
import com.uce.Tutomatch.model.Resena;
import com.uce.Tutomatch.repository.MateriaRepository;
import com.uce.Tutomatch.service.PerfilTutorService;
import com.uce.Tutomatch.service.ResenaService;
import com.uce.Tutomatch.util.AuthUtil;
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
import java.util.ArrayList;
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
        model.addAttribute("authenticated", AuthUtil.estaAutenticado(authentication));

        String materiaFilter = blankToNull(materia);
        String categoriaFilter = blankToNull(categoria);
        BigDecimal calificacionFilter = parseBigDecimal(blankToNull(minCalificacion));
        Integer semestreFilter = parseInteger(blankToNull(semestre));

        Pageable pageable = PageRequest.of(page, size);
        Page<PerfilTutor> resultadosPage = perfilTutorService.buscarTutores(
                materiaFilter, categoriaFilter, calificacionFilter, semestreFilter, pageable);
        model.addAttribute("resultados", resultadosPage.getContent());
        model.addAttribute("page", resultadosPage);
        model.addAttribute("materia", materiaFilter);
        model.addAttribute("categoria", categoriaFilter);
        model.addAttribute("minCalificacion", calificacionFilter != null ? calificacionFilter.doubleValue() : null);
        model.addAttribute("semestre", semestreFilter);

        List<String> categorias = materiaRepository.findAllByOrderByCategoriaAscNombreAsc()
                .stream()
                .map(Materia::getCategoria)
                .distinct()
                .collect(Collectors.toList());
        model.addAttribute("categorias", categorias);

        model.addAttribute("semestres", List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));

        return "resultados";
    }

    private static String blankToNull(String value) {
        return (value != null && !value.isBlank()) ? value : null;
    }

    private static BigDecimal parseBigDecimal(String value) {
        if (value == null) return null;
        try { return new BigDecimal(value); } catch (NumberFormatException e) { return null; }
    }

    private static Integer parseInteger(String value) {
        if (value == null) return null;
        try { return Integer.parseInt(value); } catch (NumberFormatException e) { return null; }
    }

    private record BloqueInfo(Long id, Integer diaSemana, String horaInicio, String horaFin) {}

    @GetMapping("/tutor/{id}")
    @Transactional(readOnly = true)
    public String verPerfilPublico(@PathVariable Long id,
                                    Authentication authentication,
                                    Model model) {
        model.addAttribute("authenticated", AuthUtil.estaAutenticado(authentication));

        try {
            PerfilTutor tutor = perfilTutorService.obtenerPorId(id);
            if (!tutor.isVerificado() || !tutor.isVisible()) {
                return "redirect:/?error=tutor_no_disponible";
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            List<BloqueInfo> bloques = new ArrayList<>();
            for (Disponibilidad d : tutor.getDisponibilidades()) {
                if (d.getEstado() == Disponibilidad.EstadoDisponibilidad.LIBRE) {
                    bloques.add(new BloqueInfo(d.getId(), d.getDiaSemana(),
                            d.getHoraInicio().format(formatter), d.getHoraFin().format(formatter)));
                }
            }

            Map<Integer, List<Map<String, Object>>> bloquesPorDia = new LinkedHashMap<>();
            List<Map<String, Object>> bloquesLista = new ArrayList<>();
            for (BloqueInfo b : bloques) {
                Map<String, Object> entry = Map.of(
                        "id", b.id(),
                        "horaInicio", b.horaInicio(),
                        "horaFin", b.horaFin()
                );
                bloquesPorDia.computeIfAbsent(b.diaSemana(), k -> new ArrayList<>()).add(entry);
                bloquesLista.add(new LinkedHashMap<>(Map.of(
                        "id", b.id(),
                        "diaSemana", b.diaSemana(),
                        "horaInicio", b.horaInicio(),
                        "horaFin", b.horaFin()
                )));
            }

            boolean esMiPerfil = AuthUtil.estaAutenticado(authentication)
                    && tutor.getUsuario().getCorreoInstitucional().equals(authentication.getName());

            List<Resena> resenas = resenaService.obtenerPorTutor(id);
            Double promedio = tutor.getCalificacionPromedio().doubleValue();

            model.addAttribute("tutor", tutor);
            model.addAttribute("bloquesPorDia", bloquesPorDia);
            model.addAttribute("bloquesLista", bloquesLista);
            model.addAttribute("dias", List.of("Lunes", "Martes", "Mi\u00e9rcoles", "Jueves", "Viernes", "S\u00e1bado", "Domingo"));
            model.addAttribute("esMiPerfil", esMiPerfil);
            model.addAttribute("resenas", resenas);
            model.addAttribute("totalResenas", (long) resenas.size());
            model.addAttribute("promedio", promedio);
        } catch (IllegalArgumentException e) {
            return "redirect:/?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }

        return "perfil-publico";
    }
}
