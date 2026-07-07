package com.uce.Tutomatch.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "perfiles_tutor")
public class PerfilTutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", unique = true, nullable = false)
    private Usuario usuario;

    @Column(name = "semestre", nullable = false)
    private Integer semestre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "verificado", nullable = false)
    private boolean verificado;

    @Column(name = "visible", nullable = false)
    private boolean visible;

    @Column(name = "calificacion_promedio", precision = 3, scale = 2)
    private BigDecimal calificacionPromedio;

    @OneToMany(mappedBy = "perfilTutor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TutorMateria> materias = new ArrayList<>();

    @OneToMany(mappedBy = "perfilTutor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Disponibilidad> disponibilidades = new ArrayList<>();

    public PerfilTutor() {
        this.verificado = false;
        this.visible = true;
        this.calificacionPromedio = BigDecimal.ZERO;
    }

    public PerfilTutor(Usuario usuario, Integer semestre, String descripcion) {
        this();
        this.usuario = usuario;
        this.semestre = semestre;
        this.descripcion = descripcion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Integer getSemestre() {
        return semestre;
    }

    public void setSemestre(Integer semestre) {
        this.semestre = semestre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isVerificado() {
        return verificado;
    }

    public void setVerificado(boolean verificado) {
        this.verificado = verificado;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public BigDecimal getCalificacionPromedio() {
        return calificacionPromedio != null ? calificacionPromedio : BigDecimal.ZERO;
    }

    public void setCalificacionPromedio(BigDecimal calificacionPromedio) {
        this.calificacionPromedio = calificacionPromedio != null ? calificacionPromedio : BigDecimal.ZERO;
    }

    public List<TutorMateria> getMaterias() {
        return materias;
    }

    public void setMaterias(List<TutorMateria> materias) {
        this.materias = materias;
    }

    public List<Disponibilidad> getDisponibilidades() {
        return disponibilidades;
    }

    public void setDisponibilidades(List<Disponibilidad> disponibilidades) {
        this.disponibilidades = disponibilidades;
    }

    @Override
    public String toString() {
        return "PerfilTutor{" +
                "id=" + id +
                ", usuarioId=" + (usuario != null ? usuario.getId() : null) +
                ", semestre=" + semestre +
                ", verificado=" + verificado +
                ", visible=" + visible +
                ", calificacionPromedio=" + calificacionPromedio +
                '}';
    }
}
