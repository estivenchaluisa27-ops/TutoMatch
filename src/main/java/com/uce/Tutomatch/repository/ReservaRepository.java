package com.uce.Tutomatch.repository;

import com.uce.Tutomatch.model.Reserva;
import com.uce.Tutomatch.model.Disponibilidad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    @Query("""
        SELECT r FROM Reserva r
        JOIN FETCH r.disponibilidad d
        JOIN FETCH d.perfilTutor pt
        JOIN FETCH pt.usuario
        JOIN FETCH r.materia
        JOIN FETCH r.solicitante
        WHERE r.solicitante.id = :solicitanteId
        ORDER BY r.fechaCreacion DESC
        """)
    List<Reserva> findBySolicitanteIdOrderByFechaCreacionDesc(Long solicitanteId);

    @Query("""
        SELECT r FROM Reserva r
        JOIN FETCH r.disponibilidad d
        JOIN FETCH d.perfilTutor pt
        JOIN FETCH pt.usuario
        JOIN FETCH r.materia
        JOIN FETCH r.solicitante
        WHERE d.perfilTutor.id = :perfilTutorId
        ORDER BY r.fechaCreacion DESC
        """)
    List<Reserva> findByDisponibilidadPerfilTutorIdOrderByFechaCreacionDesc(Long perfilTutorId);

    @Query(value = """
        SELECT r FROM Reserva r
        JOIN FETCH r.disponibilidad d
        JOIN FETCH d.perfilTutor pt
        JOIN FETCH pt.usuario
        JOIN FETCH r.materia
        JOIN FETCH r.solicitante
        WHERE r.solicitante.id = :solicitanteId
        ORDER BY r.fechaCreacion DESC
        """,
           countQuery = "SELECT COUNT(r) FROM Reserva r WHERE r.solicitante.id = :solicitanteId")
    Page<Reserva> findBySolicitanteIdOrderByFechaCreacionDesc(@Param("solicitanteId") Long solicitanteId, Pageable pageable);

    @Query(value = """
        SELECT r FROM Reserva r
        JOIN FETCH r.disponibilidad d
        JOIN FETCH d.perfilTutor pt
        JOIN FETCH pt.usuario
        JOIN FETCH r.materia
        JOIN FETCH r.solicitante
        WHERE d.perfilTutor.id = :perfilTutorId
        ORDER BY r.fechaCreacion DESC
        """,
           countQuery = "SELECT COUNT(r) FROM Reserva r WHERE r.disponibilidad.perfilTutor.id = :perfilTutorId")
    Page<Reserva> findByDisponibilidadPerfilTutorIdOrderByFechaCreacionDesc(@Param("perfilTutorId") Long perfilTutorId, Pageable pageable);

    Optional<Reserva> findByDisponibilidadIdAndEstadoNot(Long disponibilidadId, Reserva.EstadoReserva estado);

    Optional<Reserva> findByDisponibilidadId(Long disponibilidadId);

    boolean existsByDisponibilidadIdAndEstadoNot(Long disponibilidadId, Reserva.EstadoReserva estado);

    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.fechaCreacion >= :inicio AND r.fechaCreacion < :fin")
    long countReservasEnRango(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    long countByEstado(Reserva.EstadoReserva estado);
}
