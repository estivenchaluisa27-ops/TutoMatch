package com.uce.Tutomatch.dto;

import jakarta.validation.constraints.NotNull;

public class CrearReservaDTO {

    @NotNull(message = "El bloque horario es obligatorio")
    private Long disponibilidadId;

    @NotNull(message = "La materia es obligatoria")
    private Long materiaId;

    public CrearReservaDTO() {}

    public Long getDisponibilidadId() { return disponibilidadId; }
    public void setDisponibilidadId(Long disponibilidadId) { this.disponibilidadId = disponibilidadId; }
    public Long getMateriaId() { return materiaId; }
    public void setMateriaId(Long materiaId) { this.materiaId = materiaId; }
}
