package com.uce.Tutomatch.dto;

import jakarta.validation.constraints.NotBlank;

public class MateriaDTO {

    @NotBlank(message = "El nombre de la materia es obligatorio")
    private String nombre;

    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;

    private Integer semestreReferencial;

    public MateriaDTO() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public Integer getSemestreReferencial() { return semestreReferencial; }
    public void setSemestreReferencial(Integer semestreReferencial) { this.semestreReferencial = semestreReferencial; }
}
