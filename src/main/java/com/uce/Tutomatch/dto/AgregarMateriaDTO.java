package com.uce.Tutomatch.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AgregarMateriaDTO {

    @NotNull(message = "La materia es obligatoria")
    private Long materiaId;

    @Min(value = 0, message = "La tarifa no puede ser negativa")
    private int tarifaHora;

    public AgregarMateriaDTO() {}

    public Long getMateriaId() { return materiaId; }
    public void setMateriaId(Long materiaId) { this.materiaId = materiaId; }

    public int getTarifaHora() { return tarifaHora; }
    public void setTarifaHora(int tarifaHora) { this.tarifaHora = tarifaHora; }
}
