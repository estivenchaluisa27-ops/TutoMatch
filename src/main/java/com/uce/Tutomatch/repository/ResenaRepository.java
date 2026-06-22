package com.uce.Tutomatch.repository;

import com.uce.Tutomatch.model.Resena;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {
    @Query("SELECT r FROM Resena r JOIN FETCH r.reserva WHERE r.reserva.id = :reservaId")
    Optional<Resena> findByReservaId(@Param("reservaId") Long reservaId);

    @Query("SELECT r FROM Resena r JOIN FETCH r.reserva re JOIN FETCH re.solicitante WHERE re.disponibilidad.perfilTutor.id = :perfilTutorId ORDER BY r.fecha DESC")
    List<Resena> findByReservaDisponibilidadPerfilTutorIdOrderByFechaDesc(@Param("perfilTutorId") Long perfilTutorId);

    @Query(value = "SELECT r FROM Resena r JOIN FETCH r.reserva re JOIN FETCH re.solicitante WHERE re.disponibilidad.perfilTutor.id = :perfilTutorId ORDER BY r.fecha DESC",
           countQuery = "SELECT COUNT(r) FROM Resena r WHERE r.reserva.disponibilidad.perfilTutor.id = :perfilTutorId")
    Page<Resena> findByReservaDisponibilidadPerfilTutorIdOrderByFechaDesc(@Param("perfilTutorId") Long perfilTutorId, Pageable pageable);

    @Query(value = "SELECT r FROM Resena r JOIN FETCH r.reserva re JOIN FETCH re.solicitante JOIN FETCH re.disponibilidad d JOIN FETCH d.perfilTutor pt JOIN FETCH pt.usuario ORDER BY r.fecha DESC",
           countQuery = "SELECT COUNT(r) FROM Resena r")
    Page<Resena> findAllByOrderByFechaDesc(Pageable pageable);

    @Query("SELECT AVG(r.calificacion) FROM Resena r WHERE r.reserva.disponibilidad.perfilTutor.id = :perfilTutorId")
    Double promedioCalificacionPorTutor(@Param("perfilTutorId") Long perfilTutorId);
}
