package com.uce.Tutomatch.controller;

import com.uce.Tutomatch.model.Conversacion;
import com.uce.Tutomatch.model.MensajeChat;
import com.uce.Tutomatch.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/chat")
public class ChatRestController {

    private final ChatService chatService;

    public ChatRestController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/conversaciones")
    public ResponseEntity<List<Map<String, Object>>> conversaciones(Authentication auth) {
        List<Conversacion> lista = chatService.obtenerConversaciones(auth);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Conversacion c : lista) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", c.getId());
            map.put("estudianteId", c.getEstudiante().getId());
            map.put("estudianteNombre", c.getEstudiante().getNombreCompleto());
            map.put("tutorId", c.getTutor().getId());
            map.put("tutorNombre", c.getTutor().getNombreCompleto());
            map.put("ultimoMensaje", c.getUltimoMensaje());
            map.put("fechaUltimoMensaje", c.getFechaUltimoMensaje());
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{convId}/mensajes")
    public ResponseEntity<List<Map<String, Object>>> mensajes(@PathVariable Long convId, Authentication auth) {
        List<MensajeChat> mensajes = chatService.obtenerMensajes(convId, auth);
        List<Map<String, Object>> result = new ArrayList<>();
        for (MensajeChat m : mensajes) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", m.getId());
            map.put("remitenteId", m.getRemitente().getId());
            map.put("remitenteNombre", m.getRemitente().getNombreCompleto());
            map.put("contenido", m.getContenido());
            map.put("fechaEnvio", m.getFechaEnvio().toString());
            map.put("leido", m.isLeido());
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{convId}/leer")
    public ResponseEntity<Map<String, Object>> marcarLeidos(@PathVariable Long convId, Authentication auth) {
        int count = chatService.marcarLeidos(convId, auth);
        return ResponseEntity.ok(Map.of("marcados", count));
    }

    @GetMapping("/no-leidas")
    public ResponseEntity<Map<String, Object>> contarNoLeidos(Authentication auth) {
        long count = chatService.contarNoLeidos(auth);
        return ResponseEntity.ok(Map.of("noLeidas", count));
    }

    @GetMapping("/{convId}/no-leidas")
    public ResponseEntity<Map<String, Object>> contarNoLeidosConversacion(@PathVariable Long convId, Authentication auth) {
        long count = chatService.contarNoLeidos(convId, auth);
        return ResponseEntity.ok(Map.of("noLeidas", count));
    }
}
