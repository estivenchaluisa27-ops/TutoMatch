package com.uce.Tutomatch.controller;

import com.uce.Tutomatch.model.MensajeChat;
import com.uce.Tutomatch.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class ChatWsController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatWsController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.enviar")
    public void enviarMensaje(@Payload Map<String, Object> payload, Authentication auth) {
        Long conversacionId = Long.valueOf(payload.get("conversacionId").toString());
        String contenido = (String) payload.get("contenido");

        if (contenido == null || contenido.isBlank()) return;

        MensajeChat mensaje = chatService.enviarMensaje(conversacionId, contenido, auth);

        String remitente = mensaje.getRemitente().getNombreCompleto();
        String destination = "/topic/chat." + conversacionId;
        messagingTemplate.convertAndSend(destination, (Object) Map.of(
                "id", mensaje.getId(),
                "conversacionId", conversacionId,
                "remitenteId", mensaje.getRemitente().getId(),
                "remitenteNombre", remitente,
                "contenido", mensaje.getContenido(),
                "fechaEnvio", mensaje.getFechaEnvio().toString(),
                "leido", mensaje.isLeido()
        ));
    }
}
