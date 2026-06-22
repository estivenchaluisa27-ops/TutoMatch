package com.uce.Tutomatch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ConfiguracionDTO {

    @NotBlank(message = "La clave es obligatoria")
    private String clave;

    @NotBlank(message = "El valor es obligatorio")
    @Pattern(regexp = "^\\d+(\\.\\d{1,2})?$", message = "El valor debe ser un número válido")
    private String valor;

    public ConfiguracionDTO() {}

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }
    public String getValor() { return valor; }
    public void setValor(String valor) { this.valor = valor; }
}
