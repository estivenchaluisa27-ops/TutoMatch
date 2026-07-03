package com.uce.Tutomatch.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "transacciones_token")
public class TransaccionToken {

    public enum TipoTransaccion {
        BIENVENIDA,
        PAGO_TUTORIA_RECIBIDA,
        INGRESO_TUTORIA_DADA,
        DEVOLUCION_CANCELACION,
        BONUS_PRIMERA_TUTORIA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransaccion tipo;

    @Column(nullable = false)
    private int cantidad;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "referencia_reserva_id")
    private Long referenciaReservaId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fecha;

    public TransaccionToken() {
        this.fecha = LocalDateTime.now();
    }

    public TransaccionToken(Usuario usuario, TipoTransaccion tipo, int cantidad,
                           String descripcion, Long referenciaReservaId) {
        this();
        this.usuario = usuario;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.descripcion = descripcion;
        this.referenciaReservaId = referenciaReservaId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public TipoTransaccion getTipo() { return tipo; }
    public void setTipo(TipoTransaccion tipo) { this.tipo = tipo; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Long getReferenciaReservaId() { return referenciaReservaId; }
    public void setReferenciaReservaId(Long referenciaReservaId) { this.referenciaReservaId = referenciaReservaId; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
