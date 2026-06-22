package com.uce.Tutomatch.dto;

import jakarta.validation.constraints.NotNull;

public class AgregarMateriaDTO {

    @NotNull(message = "La materia es obligatoria")
    private Long materiaId;

    public AgregarMateriaDTO() {}

    public Long getMateriaId() { return materiaId; }
    public void setMateriaId(Long materiaId) { this.materiaId = materiaId; }
}
