package com.uce.Tutomatch.model;

import jakarta.persistence.*;

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

    public TutorMateria() {
    }

    public TutorMateria(PerfilTutor perfilTutor, Materia materia) {
        this.perfilTutor = perfilTutor;
        this.materia = materia;
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
}
