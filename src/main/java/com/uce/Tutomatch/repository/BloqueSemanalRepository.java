package com.uce.Tutomatch.repository;

import com.uce.Tutomatch.model.BloqueSemanal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BloqueSemanalRepository extends JpaRepository<BloqueSemanal, Long> {

    List<BloqueSemanal> findByPerfilTutorIdAndSemanaInicioOrderByDiaSemanaAscHoraAsc(
            Long perfilTutorId, LocalDate semanaInicio);

    void deleteByPerfilTutorIdAndSemanaInicio(Long perfilTutorId, LocalDate semanaInicio);

    Optional<BloqueSemanal> findByPerfilTutorIdAndSemanaInicioAndDiaSemanaAndHora(
            Long perfilTutorId, LocalDate semanaInicio, Integer diaSemana, Integer hora);

    boolean existsByPerfilTutorIdAndSemanaInicioAndDiaSemanaAndHora(
            Long perfilTutorId, LocalDate semanaInicio, Integer diaSemana, Integer hora);
}
