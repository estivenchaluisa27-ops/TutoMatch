package com.uce.Tutomatch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DescripcionDTO {

    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(max = 1000, message = "La descripción debe tener máximo 1000 caracteres")
    private String descripcion;

    public DescripcionDTO() {}

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
