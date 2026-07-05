package com.uce.Tutomatch.controller;

import com.uce.Tutomatch.dto.GuardarBloquesDTO;
import com.uce.Tutomatch.model.TutorMateria;
import com.uce.Tutomatch.service.SemanalDisponibilidadService;
import com.uce.Tutomatch.util.AuthUtil;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/tutor")
public class TutorSemanalController {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM");
    private static final DateTimeFormatter FORMATO_FECHA_LARGO = DateTimeFormatter.ofPattern("d 'de' MMMM, yyyy");
    private static final List<String> NOMBRES_DIAS_CORTOS = List.of(
            "Lun", "Mar", "Mi\u00e9", "Jue", "Vie", "S\u00e1b", "Dom"
    );
    private static final List<Integer> HORAS = IntStream.rangeClosed(7, 19)
            .boxed().collect(Collectors.toList());

    private final SemanalDisponibilidadService semanalService;
    private final AuthUtil authUtil;

    public TutorSemanalController(SemanalDisponibilidadService semanalService,
                                   AuthUtil authUtil) {
        this.semanalService = semanalService;
        this.authUtil = authUtil;
    }

    @GetMapping("/disponibilidad/semanal")
    public String verSemana(Authentication auth, Model model,
                            @RequestParam(required = false) String semana) {
        if (!AuthUtil.estaAutenticado(auth)) {
            return "redirect:/auth/login";
        }
        try {
            Long usuarioId = authUtil.obtenerUsuarioId(auth);
            semanalService.obtenerPerfilPorUsuarioId(usuarioId);

            LocalDate hoy = LocalDate.now();
            LocalDate semanaInicio;
            if (semana != null && !semana.isBlank()) {
                semanaInicio = LocalDate.parse(semana);
            } else {
                semanaInicio = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            }

            LocalDate semanaFin = semanaInicio.plusDays(6);
            Map<Integer, Set<Integer>> bloquesPorDia = semanalService.obtenerSemana(usuarioId, semanaInicio);
            Map<String, List<Long>> bloquesMaterias = semanalService.obtenerMateriasPorBloque(usuarioId, semanaInicio);
            List<TutorMateria> tutorMaterias = semanalService.obtenerTutorMaterias(usuarioId);

            List<Map<String, Object>> diasInfo = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                LocalDate fecha = semanaInicio.plusDays(i);
                Map<String, Object> info = new LinkedHashMap<>();
                info.put("diaSemana", i + 1);
                info.put("nombre", NOMBRES_DIAS_CORTOS.get(i));
                info.put("fecha", fecha.format(FORMATO_FECHA));
                info.put("esHoy", fecha.equals(hoy));
                diasInfo.add(info);
            }

            String inicioFormato = semanaInicio.format(FORMATO_FECHA_LARGO);
            String finFormato = semanaFin.format(FORMATO_FECHA_LARGO);
            model.addAttribute("semanaInicio", semanaInicio.toString());
            model.addAttribute("semanaFin", semanaFin.toString());
            model.addAttribute("semanaRango", inicioFormato + " \u2013 " + finFormato);
            // Defensa: asegurar que bloquesPorDia tenga entrada para cada día 1..7
            for (int i = 1; i <= 7; i++) {
                bloquesPorDia.putIfAbsent(i, Collections.emptySet());
            }
            model.addAttribute("diasInfo", diasInfo);
            model.addAttribute("horas", HORAS);
            model.addAttribute("bloquesPorDia", bloquesPorDia);
            model.addAttribute("bloquesMaterias", bloquesMaterias);
            model.addAttribute("tutorMaterias", tutorMaterias);
            model.addAttribute("esSemanaActual", semanaInicio.equals(hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))));
            model.addAttribute("semanaAnterior", semanaInicio.minusDays(7).toString());
            model.addAttribute("semanaSiguiente", semanaInicio.plusDays(7).toString());
            model.addAttribute("authenticated", true);
        } catch (IllegalArgumentException e) {
            return "redirect:/tutor/perfil?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
        return "configurar-disponibilidad";
    }

    @PostMapping("/disponibilidad/semanal/guardar")
    @ResponseBody
    public Map<String, Object> guardarSemana(Authentication auth, @RequestBody GuardarBloquesDTO dto) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long usuarioId = authUtil.obtenerUsuarioId(auth);
            LocalDate semanaInicio = LocalDate.parse(dto.getSemanaInicio());

            Map<Integer, Map<Integer, List<Long>>> celdas = new HashMap<>();
            for (GuardarBloquesDTO.CeldaDTO celda : dto.getBloques()) {
                celdas.computeIfAbsent(celda.getDiaSemana(), k -> new HashMap<>())
                      .put(celda.getHora(), celda.getMateriaIds() != null ? celda.getMateriaIds() : List.of());
            }

            semanalService.guardarSemana(usuarioId, semanaInicio, celdas);
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return response;
    }

    @PostMapping("/disponibilidad/semanal/limpiar")
    public String limpiarSemana(Authentication auth, @RequestParam String semana) {
        try {
            Long usuarioId = authUtil.obtenerUsuarioId(auth);
            LocalDate semanaInicio = LocalDate.parse(semana);
            semanalService.limpiarSemana(usuarioId, semanaInicio);
            return "redirect:/tutor/disponibilidad/semanal?s=" + URLEncoder.encode(semana, StandardCharsets.UTF_8) + "&success=semana_limpiada";
        } catch (Exception e) {
            return "redirect:/tutor/disponibilidad/semanal?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }
}
