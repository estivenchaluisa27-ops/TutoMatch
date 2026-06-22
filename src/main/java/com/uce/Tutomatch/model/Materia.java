package com.uce.Tutomatch.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

@Entity
@Table(name = "materias")
public class Materia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String categoria;

    @Column(name = "semestre_referencial")
    private Integer semestreReferencial;

    @Column(name = "tarifa_hora")
    private BigDecimal tarifaHora;

    @OneToMany(mappedBy = "materia")
    private List<TutorMateria> tutorMaterias = new ArrayList<>();

    public Materia() {
    }

    public Materia(String nombre, String categoria, Integer semestreReferencial) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.semestreReferencial = semestreReferencial;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Integer getSemestreReferencial() {
        return semestreReferencial;
    }

    public void setSemestreReferencial(Integer semestreReferencial) {
        this.semestreReferencial = semestreReferencial;
    }

    public List<TutorMateria> getTutorMaterias() {
        return tutorMaterias;
    }

    public void setTutorMaterias(List<TutorMateria> tutorMaterias) {
        this.tutorMaterias = tutorMaterias;
    }

    public BigDecimal getTarifaHora() {
        return tarifaHora;
    }

    public void setTarifaHora(BigDecimal tarifaHora) {
        this.tarifaHora = tarifaHora;
    }
    @Override
    public String toString() {
        return "Materia{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", categoria='" + categoria + '\'' +
                ", semestreReferencial=" + semestreReferencial +
                ", tarifaHora=" + tarifaHora +
                '}';
    }

}
