package com.uce.Tutomatch.repository;

import com.uce.Tutomatch.model.MensajeChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MensajeChatRepository extends JpaRepository<MensajeChat, Long> {

    List<MensajeChat> findByConversacionIdOrderByFechaEnvioAsc(Long conversacionId);

    @Query("SELECT COUNT(m) FROM MensajeChat m WHERE (m.conversacion.estudiante.id = :userId OR m.conversacion.tutor.id = :userId) AND m.remitente.id != :userId AND m.leido = false")
    long countNoLeidos(@Param("userId") Long userId);

    @Query("SELECT COUNT(m) FROM MensajeChat m WHERE m.conversacion.id = :convId AND m.remitente.id != :userId AND m.leido = false")
    long countNoLeidosPorConversacion(@Param("convId") Long convId, @Param("userId") Long userId);

    @Modifying
    @Query("UPDATE MensajeChat m SET m.leido = true WHERE m.conversacion.id = :convId AND m.remitente.id != :userId AND m.leido = false")
    int marcarLeidos(@Param("convId") Long convId, @Param("userId") Long userId);
}
