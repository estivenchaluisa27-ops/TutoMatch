package com.uce.Tutomatch.service;

import com.uce.Tutomatch.model.Materia;
import com.uce.Tutomatch.model.PerfilTutor;
import com.uce.Tutomatch.model.TutorMateria;
import com.uce.Tutomatch.repository.MateriaRepository;
import com.uce.Tutomatch.repository.PerfilTutorRepository;
import com.uce.Tutomatch.repository.TutorMateriaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PerfilTutorService {

    private final PerfilTutorRepository perfilTutorRepository;
    private final MateriaRepository materiaRepository;
    private final TutorMateriaRepository tutorMateriaRepository;

    public PerfilTutorService(PerfilTutorRepository perfilTutorRepository,
                              MateriaRepository materiaRepository,
                              TutorMateriaRepository tutorMateriaRepository) {
        this.perfilTutorRepository = perfilTutorRepository;
        this.materiaRepository = materiaRepository;
        this.tutorMateriaRepository = tutorMateriaRepository;
    }

    public PerfilTutor obtenerPorUsuarioId(Long usuarioId) {
        return perfilTutorRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de tutor no encontrado"));
    }

    @Transactional
    public PerfilTutor actualizarDescripcion(Long usuarioId, String descripcion) {
        PerfilTutor perfil = obtenerPorUsuarioId(usuarioId);
        perfil.setDescripcion(descripcion);
        return perfilTutorRepository.save(perfil);
    }

    @Transactional
    public PerfilTutor actualizarSemestre(Long usuarioId, Integer semestre) {
        if (semestre < 1 || semestre > 12) {
            throw new IllegalArgumentException("El semestre debe estar entre 1 y 12");
        }
        PerfilTutor perfil = obtenerPorUsuarioId(usuarioId);
        perfil.setSemestre(semestre);
        return perfilTutorRepository.save(perfil);
    }

    @Transactional
    public TutorMateria agregarMateria(Long usuarioId, Long materiaId, int tarifaHora) {
        PerfilTutor perfil = obtenerPorUsuarioId(usuarioId);
        Materia materia = materiaRepository.findById(materiaId)
                .orElseThrow(() -> new IllegalArgumentException("Materia no encontrada"));

        boolean yaTiene = perfil.getMaterias().stream()
                .anyMatch(tm -> tm.getMateria().getId().equals(materiaId));
        if (yaTiene) {
            throw new IllegalArgumentException("Ya tienes registrada esta materia");
        }

        TutorMateria tutorMateria = new TutorMateria(perfil, materia, tarifaHora);
        perfil.getMaterias().add(tutorMateria);
        perfilTutorRepository.save(perfil);
        return tutorMateria;
    }

    @Transactional
    public void eliminarMateria(Long usuarioId, Long materiaId) {
        PerfilTutor perfil = obtenerPorUsuarioId(usuarioId);
        perfil.getMaterias().removeIf(tm -> tm.getMateria().getId().equals(materiaId));
        perfilTutorRepository.save(perfil);
    }

    public List<TutorMateria> obtenerMaterias(Long usuarioId) {
        PerfilTutor perfil = obtenerPorUsuarioId(usuarioId);
        return perfil.getMaterias();
    }

    @Transactional
    public PerfilTutor verificarTutor(Long perfilTutorId, boolean verificado) {
        PerfilTutor perfil = perfilTutorRepository.findById(perfilTutorId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de tutor no encontrado"));
        perfil.setVerificado(verificado);
        perfil.setVisible(verificado);
        return perfilTutorRepository.save(perfil);
    }

    public List<PerfilTutor> listarTodos() {
        return perfilTutorRepository.findAll();
    }

    public Page<PerfilTutor> listarTodos(Pageable pageable) {
        return perfilTutorRepository.findAll(pageable);
    }

    public List<PerfilTutor> listarPendientesVerificacion() {
        return perfilTutorRepository.findByVerificadoFalse();
    }

    public Page<PerfilTutor> listarPendientesVerificacion(Pageable pageable) {
        return perfilTutorRepository.findByVerificadoFalse(pageable);
    }

    public Page<PerfilTutor> listarVerificados(Pageable pageable) {
        return perfilTutorRepository.findByVerificadoTrueAndVisibleTrue(pageable);
    }

    @Transactional(readOnly = true)
    public List<PerfilTutor> buscarTutores(String materia, String categoria,
                                           BigDecimal minCalificacion,
                                           Integer semestre) {
        String pattern = materia != null ? "%" + materia + "%" : null;
        return perfilTutorRepository.buscarTutores(
                pattern, categoria, minCalificacion, semestre);
    }

    @Transactional(readOnly = true)
    public Page<PerfilTutor> buscarTutores(String materia, String categoria,
                                           BigDecimal minCalificacion,
                                           Integer semestre, Pageable pageable) {
        String pattern = materia != null ? "%" + materia + "%" : null;
        return perfilTutorRepository.buscarTutores(
                pattern, categoria, minCalificacion, semestre, pageable);
    }

    @Transactional(readOnly = true)
    public List<PerfilTutor> buscarTutoresAdmin(String materia, String categoria,
                                                BigDecimal minCalificacion,
                                                Integer semestre) {
        String pattern = materia != null ? "%" + materia + "%" : null;
        return perfilTutorRepository.buscarTutoresAdmin(
                pattern, categoria, minCalificacion, semestre);
    }

    @Transactional(readOnly = true)
    public Page<PerfilTutor> buscarTutoresAdmin(String materia, String categoria,
                                                BigDecimal minCalificacion,
                                                Integer semestre, Pageable pageable) {
        String pattern = materia != null ? "%" + materia + "%" : null;
        return perfilTutorRepository.buscarTutoresAdmin(
                pattern, categoria, minCalificacion, semestre, pageable);
    }

    @Transactional(readOnly = true)
    public List<PerfilTutor> obtenerRecomendados() {
        return perfilTutorRepository.findTop6ByVerificadoTrueAndVisibleTrueOrderByCalificacionPromedioDesc()
                .stream()
                .filter(pt -> pt.getMaterias() != null && !pt.getMaterias().isEmpty())
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<PerfilTutor> obtenerRecomendados(Pageable pageable) {
        return perfilTutorRepository.findByVerificadoTrueAndVisibleTrueOrderByCalificacionPromedioDesc(pageable);
    }

    @Transactional(readOnly = true)
    public PerfilTutor obtenerPorId(Long id) {
        return perfilTutorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de tutor no encontrado"));
    }
}
