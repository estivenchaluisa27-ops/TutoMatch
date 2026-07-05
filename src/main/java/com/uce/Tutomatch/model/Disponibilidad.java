package com.uce.Tutomatch.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "disponibilidad")
public class Disponibilidad {

    public enum EstadoDisponibilidad {
        LIBRE, RESERVADO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfil_tutor_id", nullable = false)
    private PerfilTutor perfilTutor;

    @Column(name = "dia_semana", nullable = false)
    private Integer diaSemana;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoDisponibilidad estado;

    public Disponibilidad() {
        this.estado = EstadoDisponibilidad.LIBRE;
    }

    public Disponibilidad(PerfilTutor perfilTutor, Integer diaSemana, LocalTime horaInicio, LocalTime horaFin) {
        this();
        this.perfilTutor = perfilTutor;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PerfilTutor getPerfilTutor() {
        return perfilTutor;
    }

    public void setPerfilTutor(PerfilTutor perfilTutor) {
        this.perfilTutor = perfilTutor;
    }

    public Integer getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(Integer diaSemana) {
        this.diaSemana = diaSemana;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public EstadoDisponibilidad getEstado() {
        return estado;
    }

    public void setEstado(EstadoDisponibilidad estado) {
        this.estado = estado;
    }
}
