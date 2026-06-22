package com.uce.Tutomatch.repository;

import com.uce.Tutomatch.model.Disponibilidad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Long> {

    List<Disponibilidad> findByPerfilTutorIdOrderByDiaSemanaAscHoraInicioAsc(Long perfilTutorId);

    Page<Disponibilidad> findByPerfilTutorIdOrderByDiaSemanaAscHoraInicioAsc(Long perfilTutorId, Pageable pageable);

    List<Disponibilidad> findByPerfilTutorIdAndEstado(Long perfilTutorId, Disponibilidad.EstadoDisponibilidad estado);

    Page<Disponibilidad> findByPerfilTutorIdAndEstado(Long perfilTutorId, Disponibilidad.EstadoDisponibilidad estado, Pageable pageable);
}
