package com.uce.Tutomatch.controller;

import com.uce.Tutomatch.dto.CrearReservaDTO;
import com.uce.Tutomatch.model.Disponibilidad;
import com.uce.Tutomatch.model.PerfilTutor;
import com.uce.Tutomatch.model.Reserva;
import com.uce.Tutomatch.model.Usuario;
import com.uce.Tutomatch.repository.UsuarioRepository;
import com.uce.Tutomatch.service.NotificacionService;
import com.uce.Tutomatch.service.PerfilTutorService;
import com.uce.Tutomatch.service.ResenaService;
import com.uce.Tutomatch.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;
    private final UsuarioRepository usuarioRepository;
    private final PerfilTutorService perfilTutorService;
    private final NotificacionService notificacionService;
    private final ResenaService resenaService;

    public ReservaController(ReservaService reservaService,
                             UsuarioRepository usuarioRepository,
                             PerfilTutorService perfilTutorService,
                             NotificacionService notificacionService,
                             ResenaService resenaService) {
        this.reservaService = reservaService;
        this.usuarioRepository = usuarioRepository;
        this.perfilTutorService = perfilTutorService;
        this.notificacionService = notificacionService;
        this.resenaService = resenaService;
    }

    private Long obtenerUsuarioId(Authentication auth) {
        String email = auth.getName();
        return usuarioRepository.findByCorreoInstitucional(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"))
                .getId();
    }

    @GetMapping
    public String verMisReservas(Authentication auth,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  Model model) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        Long usuarioId = obtenerUsuarioId(auth);
        Pageable pageable = PageRequest.of(page, size);

        Page<Reserva> comoSolicitantePage = reservaService.obtenerComoSolicitante(usuarioId, pageable);

        Page<Reserva> comoTutorPage = Page.empty();
        try {
            comoTutorPage = reservaService.obtenerComoTutor(usuarioId, pageable);
        } catch (IllegalArgumentException ignored) { }

        List<String> dias = List.of("Lunes", "Martes", "Mi\u00e9rcoles", "Jueves", "Viernes", "S\u00e1bado", "Domingo");

        Set<Long> yaCalificadas = new HashSet<>();
        for (Reserva r : comoSolicitantePage.getContent()) {
            if (resenaService.existeResena(r.getId())) {
                yaCalificadas.add(r.getId());
            }
        }

        model.addAttribute("comoSolicitante", comoSolicitantePage.getContent());
        model.addAttribute("comoSolicitantePage", comoSolicitantePage);
        model.addAttribute("comoTutor", comoTutorPage.getContent());
        model.addAttribute("comoTutorPage", comoTutorPage);
        model.addAttribute("dias", dias);
        model.addAttribute("authenticated", true);
        model.addAttribute("yaCalificadas", yaCalificadas);

        return "mis-tutorias";
    }

    @PostMapping("/crear")
    public String crearReserva(Authentication auth,
                                @Valid @ModelAttribute CrearReservaDTO dto,
                                BindingResult result) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        if (result.hasErrors()) return "redirect:/reservas?error=verifica_los_campos";

        try {
            Long usuarioId = obtenerUsuarioId(auth);
            Reserva reserva = reservaService.crear(usuarioId, dto.getDisponibilidadId(), dto.getMateriaId());

            Usuario solicitante = reserva.getSolicitante();
            Usuario tutor = reserva.getDisponibilidad().getPerfilTutor().getUsuario();

            notificacionService.crear(solicitante,
                    com.uce.Tutomatch.model.Notificacion.TipoNotificacion.RESERVA_CREADA,
                    "Solicitaste una tutoría de " + reserva.getMateria().getNombre()
                            + " para el " + diaSemanaNombre(reserva.getDisponibilidad().getDiaSemana())
                            + " " + reserva.getDisponibilidad().getHoraInicio() + "-" + reserva.getDisponibilidad().getHoraFin());

            notificacionService.crear(tutor,
                    com.uce.Tutomatch.model.Notificacion.TipoNotificacion.RESERVA_CREADA,
                    solicitante.getNombreCompleto() + " solicitó una tutoría de "
                            + reserva.getMateria().getNombre()
                            + " para el " + diaSemanaNombre(reserva.getDisponibilidad().getDiaSemana())
                            + " " + reserva.getDisponibilidad().getHoraInicio() + "-" + reserva.getDisponibilidad().getHoraFin());

            return "redirect:/reservas?success=reserva_creada";
        } catch (Exception e) {
            return "redirect:/reservas?error=" + e.getMessage();
        }
    }

    @PostMapping("/{id}/confirmar")
    public String confirmarReserva(Authentication auth,
                                   @PathVariable Long id) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        try {
            Long usuarioId = obtenerUsuarioId(auth);
            Reserva reserva = reservaService.confirmar(id, usuarioId);

            notificacionService.crear(reserva.getSolicitante(),
                    com.uce.Tutomatch.model.Notificacion.TipoNotificacion.RESERVA_CONFIRMADA,
                    "Tu tutoría de " + reserva.getMateria().getNombre()
                            + " con " + reserva.getDisponibilidad().getPerfilTutor().getUsuario().getNombreCompleto()
                            + " ha sido confirmada para el "
                            + diaSemanaNombre(reserva.getDisponibilidad().getDiaSemana())
                            + " " + reserva.getDisponibilidad().getHoraInicio() + "-" + reserva.getDisponibilidad().getHoraFin());

            return "redirect:/reservas?success=reserva_confirmada";
        } catch (Exception e) {
            return "redirect:/reservas?error=" + e.getMessage();
        }
    }

    @PostMapping("/{id}/finalizar")
    public String finalizarReserva(Authentication auth,
                                    @PathVariable Long id) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        try {
            Long usuarioId = obtenerUsuarioId(auth);
            Reserva reserva = reservaService.finalizar(id, usuarioId);

            notificacionService.crear(reserva.getSolicitante(),
                    com.uce.Tutomatch.model.Notificacion.TipoNotificacion.RESERVA_FINALIZADA,
                    "Tu tutoría de " + reserva.getMateria().getNombre()
                            + " con " + reserva.getDisponibilidad().getPerfilTutor().getUsuario().getNombreCompleto()
                            + " ha finalizado.");

            notificacionService.crear(reserva.getDisponibilidad().getPerfilTutor().getUsuario(),
                    com.uce.Tutomatch.model.Notificacion.TipoNotificacion.RESERVA_FINALIZADA,
                    "La tutoría de " + reserva.getMateria().getNombre()
                            + " con " + reserva.getSolicitante().getNombreCompleto()
                            + " ha finalizado.");

            return "redirect:/reservas?success=tutoria_finalizada";
        } catch (Exception e) {
            return "redirect:/reservas?error=" + e.getMessage();
        }
    }

    @PostMapping("/{id}/cancelar")
    public String cancelarReserva(Authentication auth,
                                  @PathVariable Long id) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        try {
            Long usuarioId = obtenerUsuarioId(auth);
            Reserva reserva = reservaService.cancelar(id, usuarioId, false);

            notificacionService.crear(reserva.getSolicitante(),
                    com.uce.Tutomatch.model.Notificacion.TipoNotificacion.RESERVA_CANCELADA,
                    "Tu tutoría de " + reserva.getMateria().getNombre() + " fue cancelada.");

            notificacionService.crear(reserva.getDisponibilidad().getPerfilTutor().getUsuario(),
                    com.uce.Tutomatch.model.Notificacion.TipoNotificacion.RESERVA_CANCELADA,
                    "La tutoría de " + reserva.getMateria().getNombre()
                            + " con " + reserva.getSolicitante().getNombreCompleto() + " fue cancelada.");

            return "redirect:/reservas?success=reserva_cancelada";
        } catch (Exception e) {
            return "redirect:/reservas?error=" + e.getMessage();
        }
    }

    private String diaSemanaNombre(Integer dia) {
        return List.of("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo").get(dia - 1);
    }
}
