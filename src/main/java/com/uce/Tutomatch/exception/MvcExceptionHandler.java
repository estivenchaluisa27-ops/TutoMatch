package com.uce.Tutomatch.exception;

import com.uce.Tutomatch.controller.ResenaController;
import com.uce.Tutomatch.controller.ReservaController;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@ControllerAdvice(assignableTypes = {ReservaController.class, ResenaController.class})
public class MvcExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(MvcExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("MVC business error at {}: {}", request.getRequestURI(), ex.getMessage());
        String redirect = determineRedirect(request);
        return redirect + "error=" + URLEncoder.encode(ex.getMessage(), StandardCharsets.UTF_8);
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("MVC unhandled error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        String redirect = determineRedirect(request);
        return redirect + "error=" + URLEncoder.encode("Error interno del servidor", StandardCharsets.UTF_8);
    }

    private String determineRedirect(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri.startsWith("/resenas")) {
            return "redirect:/reservas?";
        }
        if (uri.contains("confirmar") || uri.contains("marcar-impartida")) {
            return "redirect:/reservas?tab=tutor&";
        }
        return "redirect:/reservas?";
    }
}
