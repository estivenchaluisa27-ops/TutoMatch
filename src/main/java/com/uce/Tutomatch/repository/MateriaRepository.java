package com.uce.Tutomatch.repository;

import com.uce.Tutomatch.model.Materia;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MateriaRepository extends JpaRepository<Materia, Long> {

    Optional<Materia> findByNombre(String nombre);

    List<Materia> findAllByOrderByCategoriaAscNombreAsc();

    List<Materia> findByCategoriaOrderByNombreAsc(String categoria);

    List<Materia> findByNombreContainingIgnoreCaseOrderByCategoriaAscNombreAsc(String nombre);

    List<Materia> findByNombreContainingIgnoreCaseAndFacultadContainingIgnoreCaseOrderByCategoriaAscNombreAsc(String nombre, String facultad);

    List<Materia> findByFacultadContainingIgnoreCaseOrderByCategoriaAscNombreAsc(String facultad);

    List<Materia> findByNombreContainingIgnoreCaseAndCategoriaOrderByCategoriaAscNombreAsc(String nombre, String categoria);

    @Query(value = """
        SELECT * FROM materia
        WHERE unaccent(nombre) ILIKE unaccent(:pattern)
        ORDER BY nombre ASC
        LIMIT :limit
        """, nativeQuery = true)
    List<Materia> buscarSugerencias(@Param("pattern") String pattern,
                                    @Param("limit") int limit);
}

