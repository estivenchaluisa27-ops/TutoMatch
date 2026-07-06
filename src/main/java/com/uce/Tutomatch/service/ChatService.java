package com.uce.Tutomatch.service;

import com.uce.Tutomatch.model.*;
import com.uce.Tutomatch.repository.*;
import com.uce.Tutomatch.util.AuthUtil;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService {

    private final ConversacionRepository conversacionRepository;
    private final MensajeChatRepository mensajeChatRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuthUtil authUtil;

    public ChatService(ConversacionRepository conversacionRepository,
                       MensajeChatRepository mensajeChatRepository,
                       UsuarioRepository usuarioRepository,
                       AuthUtil authUtil) {
        this.conversacionRepository = conversacionRepository;
        this.mensajeChatRepository = mensajeChatRepository;
        this.usuarioRepository = usuarioRepository;
        this.authUtil = authUtil;
    }

    @Transactional
    public Conversacion obtenerOCrear(Usuario estudiante, Usuario tutor) {
        return conversacionRepository.findEntreUsuarios(estudiante, tutor)
                .orElseGet(() -> conversacionRepository.save(new Conversacion(estudiante, tutor)));
    }

    @Transactional(readOnly = true)
    public List<Conversacion> obtenerConversaciones(Authentication auth) {
        Usuario usuario = authUtil.obtenerUsuario(auth);
        return conversacionRepository.findByParticipante(usuario);
    }

    @Transactional
    public MensajeChat enviarMensaje(Long conversacionId, String contenido, Authentication auth) {
        Usuario remitente = authUtil.obtenerUsuario(auth);
        Conversacion conversacion = conversacionRepository.findById(conversacionId)
                .orElseThrow(() -> new IllegalArgumentException("Conversaci\u00f3n no encontrada"));

        if (!conversacion.getEstudiante().getId().equals(remitente.getId()) &&
            !conversacion.getTutor().getId().equals(remitente.getId())) {
            throw new IllegalArgumentException("No eres participante de esta conversaci\u00f3n");
        }

        MensajeChat mensaje = new MensajeChat(conversacion, remitente, contenido);
        mensaje = mensajeChatRepository.save(mensaje);

        conversacion.setUltimoMensaje(contenido);
        conversacion.setFechaUltimoMensaje(LocalDateTime.now());
        conversacionRepository.save(conversacion);

        return mensaje;
    }

    @Transactional(readOnly = true)
    public List<MensajeChat> obtenerMensajes(Long conversacionId, Authentication auth) {
        Usuario usuario = authUtil.obtenerUsuario(auth);
        Conversacion conversacion = conversacionRepository.findById(conversacionId)
                .orElseThrow(() -> new IllegalArgumentException("Conversaci\u00f3n no encontrada"));

        if (!conversacion.getEstudiante().getId().equals(usuario.getId()) &&
            !conversacion.getTutor().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("No eres participante de esta conversaci\u00f3n");
        }

        return mensajeChatRepository.findByConversacionIdOrderByFechaEnvioAsc(conversacionId);
    }

    @Transactional
    public int marcarLeidos(Long conversacionId, Authentication auth) {
        Usuario usuario = authUtil.obtenerUsuario(auth);
        return mensajeChatRepository.marcarLeidos(conversacionId, usuario.getId());
    }

    @Transactional(readOnly = true)
    public long contarNoLeidos(Authentication auth) {
        Usuario usuario = authUtil.obtenerUsuario(auth);
        return mensajeChatRepository.countNoLeidos(usuario.getId());
    }

    @Transactional(readOnly = true)
    public long contarNoLeidos(Long conversacionId, Authentication auth) {
        Usuario usuario = authUtil.obtenerUsuario(auth);
        return mensajeChatRepository.countNoLeidosPorConversacion(conversacionId, usuario.getId());
    }
}
