package com.uce.Tutomatch.dto;

import java.util.List;

public class GuardarBloquesDTO {

    private String semanaInicio;
    private List<CeldaDTO> bloques;

    public GuardarBloquesDTO() {}

    public String getSemanaInicio() { return semanaInicio; }
    public void setSemanaInicio(String semanaInicio) { this.semanaInicio = semanaInicio; }

    public List<CeldaDTO> getBloques() { return bloques; }
    public void setBloques(List<CeldaDTO> bloques) { this.bloques = bloques; }

    public static class CeldaDTO {
        private Integer diaSemana;
        private Integer hora;

        public CeldaDTO() {}

        public Integer getDiaSemana() { return diaSemana; }
        public void setDiaSemana(Integer diaSemana) { this.diaSemana = diaSemana; }

        public Integer getHora() { return hora; }
        public void setHora(Integer hora) { this.hora = hora; }
    }
}
