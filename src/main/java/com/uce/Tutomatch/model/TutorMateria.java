package com.uce.Tutomatch.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tutor_materias")
public class TutorMateria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfil_tutor_id", nullable = false)
    private PerfilTutor perfilTutor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "materia_id", nullable = false)
    private Materia materia;

    @Column(name = "tarifa_hora", precision = 10, scale = 2, nullable = false)
    private BigDecimal tarifaHora;

    public TutorMateria() {
    }

    public TutorMateria(PerfilTutor perfilTutor, Materia materia, BigDecimal tarifaHora) {
        this.perfilTutor = perfilTutor;
        this.materia = materia;
        this.tarifaHora = tarifaHora;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PerfilTutor getPerfilTutor() {
        return perfilTutor;
    }

    public void setPerfilTutor(PerfilTutor perfilTutor) {
        this.perfilTutor = perfilTutor;
    }

    public Materia getMateria() {
        return materia;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
    }

    public BigDecimal getTarifaHora() {
        return tarifaHora;
    }

    public void setTarifaHora(BigDecimal tarifaHora) {
        this.tarifaHora = tarifaHora;
    }
}
