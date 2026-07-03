package com.uce.Tutomatch.controller;

import com.uce.Tutomatch.dto.GuardarBloquesDTO;
import com.uce.Tutomatch.repository.UsuarioRepository;
import com.uce.Tutomatch.service.SemanalDisponibilidadService;
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
    private static final List<String> NOMBRES_DIAS = List.of(
            "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"
    );
    private static final List<String> NOMBRES_DIAS_CORTOS = List.of(
            "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"
    );
    private static final List<Integer> HORAS = IntStream.rangeClosed(7, 19)
            .boxed().collect(Collectors.toList());

    private final SemanalDisponibilidadService semanalService;
    private final UsuarioRepository usuarioRepository;

    public TutorSemanalController(SemanalDisponibilidadService semanalService,
                                   UsuarioRepository usuarioRepository) {
        this.semanalService = semanalService;
        this.usuarioRepository = usuarioRepository;
    }

    private Long obtenerUsuarioId(Authentication auth) {
        String email = auth.getName();
        return usuarioRepository.findByCorreoInstitucional(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"))
                .getId();
    }

    @GetMapping("/disponibilidad/semanal")
    public String verSemana(Authentication auth, Model model,
                            @RequestParam(required = false) String semana) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        try {
            Long usuarioId = obtenerUsuarioId(auth);
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

            List<Map<String, Object>> diasInfo = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                LocalDate fecha = semanaInicio.plusDays(i);
                Map<String, Object> info = new LinkedHashMap<>();
                info.put("diaSemana", i + 1);
                info.put("nombre", NOMBRES_DIAS_CORTOS.get(i));
                info.put("nombreCompleto", NOMBRES_DIAS.get(i));
                info.put("fecha", fecha.format(FORMATO_FECHA));
                info.put("fechaIso", fecha.toString());
                info.put("esHoy", fecha.equals(hoy));
                diasInfo.add(info);
            }

            String inicioFormato = semanaInicio.format(FORMATO_FECHA_LARGO);
            String finFormato = semanaFin.format(FORMATO_FECHA_LARGO);
            model.addAttribute("semanaInicio", semanaInicio.toString());
            model.addAttribute("semanaFin", semanaFin.toString());
            model.addAttribute("semanaRango", inicioFormato + " – " + finFormato);
            model.addAttribute("diasInfo", diasInfo);
            model.addAttribute("horas", HORAS);
            model.addAttribute("bloquesPorDia", bloquesPorDia);
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
            Long usuarioId = obtenerUsuarioId(auth);
            LocalDate semanaInicio = LocalDate.parse(dto.getSemanaInicio());

            Map<Integer, Set<Integer>> celdas = dto.getBloques().stream()
                    .collect(Collectors.groupingBy(
                            GuardarBloquesDTO.CeldaDTO::getDiaSemana,
                            Collectors.mapping(GuardarBloquesDTO.CeldaDTO::getHora, Collectors.toSet())
                    ));

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
            Long usuarioId = obtenerUsuarioId(auth);
            LocalDate semanaInicio = LocalDate.parse(semana);
            semanalService.limpiarSemana(usuarioId, semanaInicio);
            return "redirect:/tutor/disponibilidad/semanal?s=" + URLEncoder.encode(semana, StandardCharsets.UTF_8) + "&success=semana_limpiada";
        } catch (Exception e) {
            return "redirect:/tutor/disponibilidad/semanal?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }
}
