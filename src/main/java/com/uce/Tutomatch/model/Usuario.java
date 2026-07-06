package com.uce.Tutomatch.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "correo_institucional", unique = true, nullable = false)
    private String correoInstitucional;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "nombre_completo", nullable = false)
    private String nombreCompleto;

    @Column(name = "rol_solicitante", nullable = false)
    private boolean rolSolicitante;

    @Column(name = "rol_tutor", nullable = false)
    private boolean rolTutor;

    @Column(name = "rol_admin", nullable = false)
    private boolean rolAdmin;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    public Usuario() {
        this.fechaCreacion = LocalDateTime.now();
        this.rolSolicitante = false;
        this.rolTutor = false;
        this.rolAdmin = false;
    }

    public Usuario(String correoInstitucional, String passwordHash, String nombreCompleto,
                   boolean rolSolicitante, boolean rolTutor, boolean rolAdmin) {
        this();
        this.correoInstitucional = correoInstitucional;
        this.passwordHash = passwordHash;
        this.nombreCompleto = nombreCompleto;
        this.rolSolicitante = rolSolicitante;
        this.rolTutor = rolTutor;
        this.rolAdmin = rolAdmin;
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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
        return "Usuario{" +
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
