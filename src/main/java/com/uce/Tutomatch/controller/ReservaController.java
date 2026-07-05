package com.uce.Tutomatch.controller;

import com.uce.Tutomatch.dto.CrearReservaDTO;
import com.uce.Tutomatch.model.Reserva;
import com.uce.Tutomatch.service.PerfilTutorService;
import com.uce.Tutomatch.service.ResenaService;
import com.uce.Tutomatch.service.ReservaPagoService;
import com.uce.Tutomatch.service.ReservaService;
import com.uce.Tutomatch.util.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Controller
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;
    private final ReservaPagoService reservaPagoService;
    private final PerfilTutorService perfilTutorService;
    private final ResenaService resenaService;
    private final AuthUtil authUtil;

    public ReservaController(ReservaService reservaService,
                             ReservaPagoService reservaPagoService,
                             PerfilTutorService perfilTutorService,
                             ResenaService resenaService,
                             AuthUtil authUtil) {
        this.reservaService = reservaService;
        this.reservaPagoService = reservaPagoService;
        this.perfilTutorService = perfilTutorService;
        this.resenaService = resenaService;
        this.authUtil = authUtil;
    }

    @GetMapping
    public String verMisReservas(Authentication auth,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @RequestParam(defaultValue = "solicitante") String tab,
                                  Model model) {
        if (!AuthUtil.estaAutenticado(auth)) {
            return "redirect:/auth/login";
        }

        Long usuarioId = authUtil.obtenerUsuarioId(auth);
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
        model.addAttribute("tab", tab);

        return "mis-tutorias";
    }

    @PostMapping("/crear")
    public String crearReserva(Authentication auth,
                                @Valid @ModelAttribute CrearReservaDTO dto,
                                BindingResult result) {
        if (!AuthUtil.estaAutenticado(auth)) {
            return "redirect:/auth/login";
        }
        if (result.hasErrors()) return "redirect:/reservas?error=verifica_los_campos";

        try {
            Long usuarioId = authUtil.obtenerUsuarioId(auth);
            reservaService.crear(usuarioId, dto.getDisponibilidadId(), dto.getMateriaId());
            return "redirect:/reservas?success=reserva_creada";
        } catch (Exception e) {
            return "redirect:/reservas?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @PostMapping("/{id}/confirmar")
    public String confirmarReserva(Authentication auth,
                                   @PathVariable Long id) {
        if (!AuthUtil.estaAutenticado(auth)) {
            return "redirect:/auth/login";
        }

        try {
            Long usuarioId = authUtil.obtenerUsuarioId(auth);
            reservaService.confirmar(id, usuarioId);
            return "redirect:/reservas?tab=tutor&success=reserva_confirmada";
        } catch (Exception e) {
            return "redirect:/reservas?tab=tutor&error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @PostMapping("/{id}/finalizar")
    public String finalizarReserva(Authentication auth,
                                    @PathVariable Long id) {
        if (!AuthUtil.estaAutenticado(auth)) {
            return "redirect:/auth/login";
        }
        try {
            Long usuarioId = authUtil.obtenerUsuarioId(auth);
            reservaService.finalizar(id, usuarioId);
            return "redirect:/reservas?success=tutoria_finalizada";
        } catch (Exception e) {
            return "redirect:/reservas?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @PostMapping("/{id}/cancelar")
    public String cancelarReserva(Authentication auth,
                                  @PathVariable Long id) {
        if (!AuthUtil.estaAutenticado(auth)) {
            return "redirect:/auth/login";
        }

        try {
            Long usuarioId = authUtil.obtenerUsuarioId(auth);
            reservaService.cancelar(id, usuarioId, false);
            return "redirect:/reservas?success=reserva_cancelada";
        } catch (Exception e) {
            return "redirect:/reservas?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @PostMapping("/{id}/marcar-impartida")
    public String marcarImpartida(Authentication auth,
                                  @PathVariable Long id) {
        if (!AuthUtil.estaAutenticado(auth)) {
            return "redirect:/auth/login";
        }

        try {
            Long usuarioId = authUtil.obtenerUsuarioId(auth);
            reservaPagoService.marcarSesionImpartida(id, usuarioId);
            return "redirect:/reservas?tab=tutor&success=sesion_marcada";
        } catch (Exception e) {
            return "redirect:/reservas?tab=tutor&error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @PostMapping("/{id}/pagar-token")
    public String pagarConToken(Authentication auth,
                                @PathVariable Long id) {
        if (!AuthUtil.estaAutenticado(auth)) {
            return "redirect:/auth/login";
        }

        try {
            Long usuarioId = authUtil.obtenerUsuarioId(auth);
            reservaPagoService.pagarConToken(id, usuarioId);
            return "redirect:/reservas?success=pago_exitoso";
        } catch (Exception e) {
            return "redirect:/reservas?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @PostMapping("/{id}/cancelar-pago")
    public String cancelarEnPendientePago(Authentication auth,
                                          @PathVariable Long id) {
        if (!AuthUtil.estaAutenticado(auth)) {
            return "redirect:/auth/login";
        }

        try {
            Long usuarioId = authUtil.obtenerUsuarioId(auth);
            reservaPagoService.cancelarEnPendientePago(id, usuarioId);
            return "redirect:/reservas?success=reserva_cancelada";
        } catch (Exception e) {
            return "redirect:/reservas?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }
}
