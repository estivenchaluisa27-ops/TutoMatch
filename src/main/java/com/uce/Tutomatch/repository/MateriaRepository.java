package com.uce.Tutomatch.repository;

import com.uce.Tutomatch.model.Materia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MateriaRepository extends JpaRepository<Materia, Long> {

    List<Materia> findAllByOrderByCategoriaAscNombreAsc();

    Page<Materia> findAllByOrderByCategoriaAscNombreAsc(Pageable pageable);

    List<Materia> findByCategoriaOrderByNombreAsc(String categoria);

    Page<Materia> findByCategoriaOrderByNombreAsc(String categoria, Pageable pageable);
}
