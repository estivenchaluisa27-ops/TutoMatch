package com.uce.Tutomatch.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "materia")
public class Materia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String facultad;

    private String carrera;

    @Column(name = "nivelDesercion")
    private String nivelDesercion;

    private String transversalidad;

    @Column(nullable = false)
    private String nombre;

    private String categoria;

    @Column(name = "semestreReferencial")
    private Integer semestreReferencial;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "icono", columnDefinition = "TEXT")
    private String icono;

    @OneToMany(mappedBy = "materia")
    private List<TutorMateria> tutorMaterias = new ArrayList<>();

    public Materia() {
    }

    public Materia(String nombre, String categoria, Integer semestreReferencial) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.semestreReferencial = semestreReferencial;
    }

    public Materia(String nombre, String categoria, Integer semestreReferencial, String descripcion, String icono, String facultad, String carrera, String nivelDesercion, String transversalidad) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.semestreReferencial = semestreReferencial;
        this.descripcion = descripcion;
        this.icono = icono;
        this.facultad = facultad;
        this.carrera = carrera;
        this.nivelDesercion = nivelDesercion;
        this.transversalidad = transversalidad;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFacultad() {
        return facultad;
    }

    public void setFacultad(String facultad) {
        this.facultad = facultad;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public String getNivelDesercion() {
        return nivelDesercion;
    }

    public void setNivelDesercion(String nivelDesercion) {
        this.nivelDesercion = nivelDesercion;
    }

    public String getTransversalidad() {
        return transversalidad;
    }

    public void setTransversalidad(String transversalidad) {
        this.transversalidad = transversalidad;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public List<TutorMateria> getTutorMaterias() {
        return tutorMaterias;
    }

    public void setTutorMaterias(List<TutorMateria> tutorMaterias) {
        this.tutorMaterias = tutorMaterias;
    }

    @Override
    public String toString() {
        return "Materia{" +
                "id=" + id +
                ", facultad='" + facultad + '\'' +
                ", carrera='" + carrera + '\'' +
                ", nombre='" + nombre + '\'' +
                ", categoria='" + categoria + '\'' +
                ", semestreReferencial=" + semestreReferencial +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }

}
