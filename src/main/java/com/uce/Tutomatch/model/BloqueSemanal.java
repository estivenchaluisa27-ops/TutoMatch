package com.uce.Tutomatch.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bloques_semanales")
public class BloqueSemanal {

    public enum EstadoBloque {
        DISPONIBLE, RESERVADO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfil_tutor_id", nullable = false)
    private PerfilTutor perfilTutor;

    @Column(name = "semana_inicio", nullable = false)
    private LocalDate semanaInicio;

    @Column(name = "dia_semana", nullable = false)
    private Integer diaSemana;

    @Column(name = "hora", nullable = false)
    private Integer hora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoBloque estado;

    @ManyToMany
    @JoinTable(name = "bloque_materias",
               joinColumns = @JoinColumn(name = "bloque_semanal_id"),
               inverseJoinColumns = @JoinColumn(name = "tutor_materia_id"))
    private List<TutorMateria> materias = new ArrayList<>();

    public BloqueSemanal() {
        this.estado = EstadoBloque.DISPONIBLE;
    }

    public BloqueSemanal(PerfilTutor perfilTutor, LocalDate semanaInicio, Integer diaSemana, Integer hora) {
        this();
        this.perfilTutor = perfilTutor;
        this.semanaInicio = semanaInicio;
        this.diaSemana = diaSemana;
        this.hora = hora;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PerfilTutor getPerfilTutor() { return perfilTutor; }
    public void setPerfilTutor(PerfilTutor perfilTutor) { this.perfilTutor = perfilTutor; }

    public LocalDate getSemanaInicio() { return semanaInicio; }
    public void setSemanaInicio(LocalDate semanaInicio) { this.semanaInicio = semanaInicio; }

    public Integer getDiaSemana() { return diaSemana; }
    public void setDiaSemana(Integer diaSemana) { this.diaSemana = diaSemana; }

    public Integer getHora() { return hora; }
    public void setHora(Integer hora) { this.hora = hora; }

    public EstadoBloque getEstado() { return estado; }
    public void setEstado(EstadoBloque estado) { this.estado = estado; }

    public List<TutorMateria> getMaterias() { return materias; }
    public void setMaterias(List<TutorMateria> materias) { this.materias = materias; }
}
