package com.uce.Tutomatch.repository;

import com.uce.Tutomatch.model.TutorMateria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TutorMateriaRepository extends JpaRepository<TutorMateria, Long> {

    List<TutorMateria> findByPerfilTutorId(Long perfilTutorId);

    Page<TutorMateria> findByPerfilTutorId(Long perfilTutorId, Pageable pageable);

    void deleteByPerfilTutorIdAndMateriaId(Long perfilTutorId, Long materiaId);

    Optional<TutorMateria> findByPerfilTutorIdAndMateriaId(Long perfilTutorId, Long materiaId);
}
