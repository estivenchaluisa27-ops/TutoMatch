package com.uce.Tutomatch.controller;

import com.uce.Tutomatch.model.Materia;
import com.uce.Tutomatch.model.PerfilTutor;
import com.uce.Tutomatch.repository.MateriaRepository;
import com.uce.Tutomatch.service.PerfilTutorService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final MateriaRepository materiaRepository;
    private final PerfilTutorService perfilTutorService;

    public HomeController(MateriaRepository materiaRepository,
                          PerfilTutorService perfilTutorService) {
        this.materiaRepository = materiaRepository;
        this.perfilTutorService = perfilTutorService;
    }

    @GetMapping("/")
    public String home(Authentication authentication, Model model) {
        model.addAttribute("authenticated", authentication != null && authentication.isAuthenticated());

        List<Materia> todasMaterias = materiaRepository.findAllByOrderByCategoriaAscNombreAsc();
        Map<String, List<Materia>> materiasPorCategoria = todasMaterias.stream()
                .collect(Collectors.groupingBy(
                        Materia::getCategoria,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
        model.addAttribute("materiasPorCategoria", materiasPorCategoria);

        List<PerfilTutor> recomendados = perfilTutorService.obtenerRecomendados();
        model.addAttribute("recomendados", recomendados);

        return "home";
    }
}
