package com.uce.Tutomatch.repository;

import com.uce.Tutomatch.model.Conversacion;
import com.uce.Tutomatch.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConversacionRepository extends JpaRepository<Conversacion, Long> {

    @Query("SELECT c FROM Conversacion c WHERE (c.estudiante = :e AND c.tutor = :t) OR (c.estudiante = :t AND c.tutor = :e)")
    Optional<Conversacion> findEntreUsuarios(@Param("e") Usuario estudiante, @Param("t") Usuario tutor);

    @Query("SELECT c FROM Conversacion c WHERE c.estudiante = :u OR c.tutor = :u ORDER BY c.fechaUltimoMensaje DESC NULLS LAST")
    List<Conversacion> findByParticipante(@Param("u") Usuario usuario);
}
