package com.uce.Tutomatch.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class AgregarMateriaDTO {

    @NotNull(message = "La materia es obligatoria")
    private Long materiaId;

    @NotNull(message = "La tarifa por hora es obligatoria")
    @DecimalMin(value = "1.00", message = "La tarifa mínima es $1.00")
    @DecimalMax(value = "100.00", message = "La tarifa máxima es $100.00")
    private BigDecimal tarifaHora;

    public AgregarMateriaDTO() {}

    public Long getMateriaId() { return materiaId; }
    public void setMateriaId(Long materiaId) { this.materiaId = materiaId; }
    public BigDecimal getTarifaHora() { return tarifaHora; }
    public void setTarifaHora(BigDecimal tarifaHora) { this.tarifaHora = tarifaHora; }
}
