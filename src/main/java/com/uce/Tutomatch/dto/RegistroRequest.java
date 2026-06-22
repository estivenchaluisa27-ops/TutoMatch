package com.uce.Tutomatch.dto;

import com.uce.Tutomatch.validation.InstitutionalEmail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegistroRequest {

    @NotBlank(message = "El correo institucional es obligatorio")
    @InstitutionalEmail
    private String correoInstitucional;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotBlank(message = "El nombre completo es obligatorio")
    private String nombreCompleto;

    private boolean rolSolicitante = true;

    private boolean rolTutor = false;

    public RegistroRequest() {
    }

    public String getCorreoInstitucional() {
        return correoInstitucional;
    }

    public void setCorreoInstitucional(String correoInstitucional) {
        this.correoInstitucional = correoInstitucional;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public boolean isRolSolicitante() {
        return rolSolicitante;
    }

    public void setRolSolicitante(boolean rolSolicitante) {
        this.rolSolicitante = rolSolicitante;
    }

    public boolean isRolTutor() {
        return rolTutor;
    }

    public void setRolTutor(boolean rolTutor) {
        this.rolTutor = rolTutor;
    }

    @Override
    public String toString() {
        return "RegistroRequest{" +
                "correoInstitucional='" + correoInstitucional + '\'' +
                ", nombreCompleto='" + nombreCompleto + '\'' +
                ", rolSolicitante=" + rolSolicitante +
                ", rolTutor=" + rolTutor +
                '}';
    }
}
