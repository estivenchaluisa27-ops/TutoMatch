package com.uce.Tutomatch.repository;

import com.uce.Tutomatch.model.PerfilTutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PerfilTutorRepository extends JpaRepository<PerfilTutor, Long> {

    @Query("SELECT pt FROM PerfilTutor pt JOIN FETCH pt.usuario WHERE pt.usuario.id = :usuarioId")
    Optional<PerfilTutor> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("SELECT pt FROM PerfilTutor pt JOIN FETCH pt.usuario WHERE pt.verificado = false")
    List<PerfilTutor> findByVerificadoFalse();

    @Query(value = "SELECT pt FROM PerfilTutor pt JOIN FETCH pt.usuario WHERE pt.verificado = false",
           countQuery = "SELECT COUNT(pt) FROM PerfilTutor pt WHERE pt.verificado = false")
    Page<PerfilTutor> findByVerificadoFalse(Pageable pageable);

    @Query("SELECT DISTINCT pt FROM PerfilTutor pt " +
           "JOIN FETCH pt.usuario " +
           "JOIN pt.materias tm " +
           "JOIN tm.materia m " +
           "WHERE pt.verificado = true AND pt.visible = true " +
           "AND (:nombre IS NULL OR LOWER(m.nombre) LIKE :nombre OR LOWER(m.categoria) LIKE :nombre) " +
           "AND (:categoria IS NULL OR m.categoria = :categoria) " +
           "AND (:minCalificacion IS NULL OR pt.calificacionPromedio >= :minCalificacion) " +
           "AND (:semestre IS NULL OR pt.semestre = :semestre)")
    List<PerfilTutor> buscarTutores(@Param("nombre") String nombre,
                                    @Param("categoria") String categoria,
                                    @Param("minCalificacion") BigDecimal minCalificacion,
                                    @Param("semestre") Integer semestre);

    @Query("SELECT DISTINCT pt FROM PerfilTutor pt " +
           "JOIN FETCH pt.usuario " +
           "JOIN pt.materias tm " +
           "JOIN tm.materia m " +
           "WHERE pt.verificado = true AND pt.visible = true " +
           "AND (:nombre IS NULL OR LOWER(m.nombre) LIKE :nombre OR LOWER(m.categoria) LIKE :nombre) " +
           "AND (:categoria IS NULL OR m.categoria = :categoria) " +
           "AND (:minCalificacion IS NULL OR pt.calificacionPromedio >= :minCalificacion) " +
           "AND (:semestre IS NULL OR pt.semestre = :semestre)")
    Page<PerfilTutor> buscarTutores(@Param("nombre") String nombre,
                                    @Param("categoria") String categoria,
                                    @Param("minCalificacion") BigDecimal minCalificacion,
                                    @Param("semestre") Integer semestre,
                                    Pageable pageable);

    @Query("SELECT pt FROM PerfilTutor pt JOIN FETCH pt.usuario WHERE pt.verificado = true AND pt.visible = true ORDER BY pt.calificacionPromedio DESC")
    List<PerfilTutor> findTop6ByVerificadoTrueAndVisibleTrueOrderByCalificacionPromedioDesc();

    @Query(value = "SELECT pt FROM PerfilTutor pt JOIN FETCH pt.usuario WHERE pt.verificado = true AND pt.visible = true ORDER BY pt.calificacionPromedio DESC",
           countQuery = "SELECT COUNT(pt) FROM PerfilTutor pt WHERE pt.verificado = true AND pt.visible = true")
    Page<PerfilTutor> findByVerificadoTrueAndVisibleTrueOrderByCalificacionPromedioDesc(Pageable pageable);

    long countByVerificadoTrue();

    long countByVerificadoFalse();

    @Query(value = "SELECT pt FROM PerfilTutor pt JOIN FETCH pt.usuario WHERE pt.verificado = true AND pt.visible = true",
           countQuery = "SELECT COUNT(pt) FROM PerfilTutor pt WHERE pt.verificado = true AND pt.visible = true")
    Page<PerfilTutor> findByVerificadoTrueAndVisibleTrue(Pageable pageable);

}
