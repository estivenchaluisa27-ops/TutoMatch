package com.uce.Tutomatch.dto;

import jakarta.validation.constraints.NotBlank;

public class MateriaDTO {

    @NotBlank(message = "El nombre de la materia es obligatorio")
    private String nombre;

    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;

    private Integer semestreReferencial;

    private String descripcion;

    private String icono;

    public MateriaDTO() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public Integer getSemestreReferencial() { return semestreReferencial; }
    public void setSemestreReferencial(Integer semestreReferencial) { this.semestreReferencial = semestreReferencial; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getIcono() { return icono; }
    public void setIcono(String icono) { this.icono = icono; }
}
