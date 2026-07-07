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

    // ════════════════════════════════════════════════════════════
    // Búsqueda pública — native SQL con unaccent() + ILIKE
    // Requiere: CREATE EXTENSION IF NOT EXISTS unaccent;
    // Busca en: materia.nombre, materia.categoria, usuario.nombre_completo
    // ════════════════════════════════════════════════════════════

    @Query(value = """
        SELECT DISTINCT pt.* FROM perfiles_tutor pt
        JOIN usuarios u ON pt.usuario_id = u.id
        JOIN tutor_materias tm ON tm.perfil_tutor_id = pt.id
        JOIN materia m ON tm.materia_id = m.id
        WHERE pt.verificado = true AND pt.visible = true
        AND (:nombre IS NULL
             OR unaccent(m.nombre) ILIKE unaccent(:nombre)
             OR unaccent(m.categoria) ILIKE unaccent(:nombre)
             OR unaccent(u.nombre_completo) ILIKE unaccent(:nombre))
        AND (:categoria IS NULL OR unaccent(m.categoria) = unaccent(:categoria))
        AND (:minCalificacion IS NULL OR pt.calificacion_promedio >= :minCalificacion)
        AND (:semestre IS NULL OR pt.semestre = :semestre)
        ORDER BY pt.calificacion_promedio DESC
        """,
            nativeQuery = true)
    List<PerfilTutor> buscarTutores(@Param("nombre") String nombre,
                                   @Param("categoria") String categoria,
                                   @Param("minCalificacion") BigDecimal minCalificacion,
                                   @Param("semestre") Integer semestre);

    @Query(value = """
        SELECT DISTINCT pt.* FROM perfiles_tutor pt
        JOIN usuarios u ON pt.usuario_id = u.id
        JOIN tutor_materias tm ON tm.perfil_tutor_id = pt.id
        JOIN materia m ON tm.materia_id = m.id
        WHERE pt.verificado = true AND pt.visible = true
        AND (:nombre IS NULL
             OR unaccent(m.nombre) ILIKE unaccent(:nombre)
             OR unaccent(m.categoria) ILIKE unaccent(:nombre)
             OR unaccent(u.nombre_completo) ILIKE unaccent(:nombre))
        AND (:categoria IS NULL OR unaccent(m.categoria) = unaccent(:categoria))
        AND (:minCalificacion IS NULL OR pt.calificacion_promedio >= :minCalificacion)
        AND (:semestre IS NULL OR pt.semestre = :semestre)
        ORDER BY pt.calificacion_promedio DESC
        """,
            countQuery = """
        SELECT COUNT(DISTINCT pt.id) FROM perfiles_tutor pt
        JOIN usuarios u ON pt.usuario_id = u.id
        JOIN tutor_materias tm ON tm.perfil_tutor_id = pt.id
        JOIN materia m ON tm.materia_id = m.id
        WHERE pt.verificado = true AND pt.visible = true
        AND (:nombre IS NULL
             OR unaccent(m.nombre) ILIKE unaccent(:nombre)
             OR unaccent(m.categoria) ILIKE unaccent(:nombre)
             OR unaccent(u.nombre_completo) ILIKE unaccent(:nombre))
        AND (:categoria IS NULL OR unaccent(m.categoria) = unaccent(:categoria))
        AND (:minCalificacion IS NULL OR pt.calificacion_promedio >= :minCalificacion)
        AND (:semestre IS NULL OR pt.semestre = :semestre)
        """,
            nativeQuery = true)
    Page<PerfilTutor> buscarTutores(@Param("nombre") String nombre,
                                    @Param("categoria") String categoria,
                                    @Param("minCalificacion") BigDecimal minCalificacion,
                                    @Param("semestre") Integer semestre,
                                    Pageable pageable);

    @Query("SELECT DISTINCT pt FROM PerfilTutor pt JOIN FETCH pt.usuario LEFT JOIN FETCH pt.materias WHERE pt.verificado = true AND pt.visible = true ORDER BY pt.calificacionPromedio DESC")
    List<PerfilTutor> findTop6ByVerificadoTrueAndVisibleTrueOrderByCalificacionPromedioDesc();

    @Query(value = "SELECT DISTINCT pt FROM PerfilTutor pt JOIN FETCH pt.usuario LEFT JOIN FETCH pt.materias WHERE pt.verificado = true AND pt.visible = true ORDER BY pt.calificacionPromedio DESC",
           countQuery = "SELECT COUNT(pt) FROM PerfilTutor pt WHERE pt.verificado = true AND pt.visible = true")
    Page<PerfilTutor> findByVerificadoTrueAndVisibleTrueOrderByCalificacionPromedioDesc(Pageable pageable);

    long countByVerificadoTrue();

    long countByVerificadoFalse();

    @Query(value = "SELECT pt FROM PerfilTutor pt JOIN FETCH pt.usuario WHERE pt.verificado = true AND pt.visible = true",
           countQuery = "SELECT COUNT(pt) FROM PerfilTutor pt WHERE pt.verificado = true AND pt.visible = true")
    Page<PerfilTutor> findByVerificadoTrueAndVisibleTrue(Pageable pageable);

    // ════════════════════════════════════════════════════════════
    // Búsqueda admin — LEFT JOIN, sin filtro verificado/visible
    // ════════════════════════════════════════════════════════════

    @Query(value = """
        SELECT DISTINCT pt.* FROM perfiles_tutor pt
        JOIN usuarios u ON pt.usuario_id = u.id
        LEFT JOIN tutor_materias tm ON tm.perfil_tutor_id = pt.id
        LEFT JOIN materia m ON tm.materia_id = m.id
        WHERE (:nombre IS NULL
             OR unaccent(m.nombre) ILIKE unaccent(:nombre)
             OR unaccent(m.categoria) ILIKE unaccent(:nombre)
             OR unaccent(u.nombre_completo) ILIKE unaccent(:nombre))
        AND (:categoria IS NULL OR unaccent(m.categoria) = unaccent(:categoria))
        AND (:minCalificacion IS NULL OR pt.calificacion_promedio >= :minCalificacion)
        AND (:semestre IS NULL OR pt.semestre = :semestre)
        ORDER BY u.nombre_completo ASC
        """,
            nativeQuery = true)
    List<PerfilTutor> buscarTutoresAdmin(@Param("nombre") String nombre,
                                         @Param("categoria") String categoria,
                                         @Param("minCalificacion") BigDecimal minCalificacion,
                                         @Param("semestre") Integer semestre);

    @Query(value = """
        SELECT DISTINCT pt.* FROM perfiles_tutor pt
        JOIN usuarios u ON pt.usuario_id = u.id
        LEFT JOIN tutor_materias tm ON tm.perfil_tutor_id = pt.id
        LEFT JOIN materia m ON tm.materia_id = m.id
        WHERE (:nombre IS NULL
             OR unaccent(m.nombre) ILIKE unaccent(:nombre)
             OR unaccent(m.categoria) ILIKE unaccent(:nombre)
             OR unaccent(u.nombre_completo) ILIKE unaccent(:nombre))
        AND (:categoria IS NULL OR unaccent(m.categoria) = unaccent(:categoria))
        AND (:minCalificacion IS NULL OR pt.calificacion_promedio >= :minCalificacion)
        AND (:semestre IS NULL OR pt.semestre = :semestre)
        ORDER BY u.nombre_completo ASC
        """,
            countQuery = """
        SELECT COUNT(DISTINCT pt.id) FROM perfiles_tutor pt
        JOIN usuarios u ON pt.usuario_id = u.id
        LEFT JOIN tutor_materias tm ON tm.perfil_tutor_id = pt.id
        LEFT JOIN materia m ON tm.materia_id = m.id
        WHERE (:nombre IS NULL
             OR unaccent(m.nombre) ILIKE unaccent(:nombre)
             OR unaccent(m.categoria) ILIKE unaccent(:nombre)
             OR unaccent(u.nombre_completo) ILIKE unaccent(:nombre))
        AND (:categoria IS NULL OR unaccent(m.categoria) = unaccent(:categoria))
        AND (:minCalificacion IS NULL OR pt.calificacion_promedio >= :minCalificacion)
        AND (:semestre IS NULL OR pt.semestre = :semestre)
        """,
            nativeQuery = true)
    Page<PerfilTutor> buscarTutoresAdmin(@Param("nombre") String nombre,
                                         @Param("categoria") String categoria,
                                         @Param("minCalificacion") BigDecimal minCalificacion,
                                         @Param("semestre") Integer semestre,
                                         Pageable pageable);
}
