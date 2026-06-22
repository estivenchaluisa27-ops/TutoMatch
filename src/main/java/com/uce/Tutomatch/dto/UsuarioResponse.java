package com.uce.Tutomatch.dto;

import com.uce.Tutomatch.model.Usuario;
import java.time.LocalDateTime;

public class UsuarioResponse {

    private Long id;

    private String correoInstitucional;

    private String nombreCompleto;

    private boolean rolSolicitante;

    private boolean rolTutor;

    private boolean rolAdmin;

    private LocalDateTime fechaCreacion;

    public UsuarioResponse() {
    }

    public UsuarioResponse(Long id, String correoInstitucional, String nombreCompleto,
                           boolean rolSolicitante, boolean rolTutor, boolean rolAdmin,
                           LocalDateTime fechaCreacion) {
        this.id = id;
        this.correoInstitucional = correoInstitucional;
        this.nombreCompleto = nombreCompleto;
        this.rolSolicitante = rolSolicitante;
        this.rolTutor = rolTutor;
        this.rolAdmin = rolAdmin;
        this.fechaCreacion = fechaCreacion;
    }

    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getCorreoInstitucional(),
                usuario.getNombreCompleto(),
                usuario.isRolSolicitante(),
                usuario.isRolTutor(),
                usuario.isRolAdmin(),
                usuario.getFechaCreacion()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCorreoInstitucional() {
        return correoInstitucional;
    }

    public void setCorreoInstitucional(String correoInstitucional) {
        this.correoInstitucional = correoInstitucional;
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

    public boolean isRolAdmin() {
        return rolAdmin;
    }

    public void setRolAdmin(boolean rolAdmin) {
        this.rolAdmin = rolAdmin;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @Override
    public String toString() {
        return "UsuarioResponse{" +
                "id=" + id +
                ", correoInstitucional='" + correoInstitucional + '\'' +
                ", nombreCompleto='" + nombreCompleto + '\'' +
                ", rolSolicitante=" + rolSolicitante +
                ", rolTutor=" + rolTutor +
                ", rolAdmin=" + rolAdmin +
                ", fechaCreacion=" + fechaCreacion +
                '}';
    }
}
