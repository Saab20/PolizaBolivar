package co.com.segurosbolivar.polizaapi.domain;

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
import java.math.BigDecimal;

/**
 * Riesgo asegurado dentro de una poliza (el inmueble arrendado).
 * Una poliza individual admite exactamente uno; una colectiva, varios.
 */
@Entity
@Table(name = "riesgo")
public class Riesgo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Direccion del inmueble asegurado. */
    @Column(nullable = false)
    private String direccionInmueble;

    /** Canon mensual asociado a este riesgo en particular. */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal canonAsociado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoRiesgo estado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "poliza_id", nullable = false)
    private Poliza poliza;

    protected Riesgo() {
        // Requerido por JPA.
    }

    public Riesgo(String direccionInmueble, BigDecimal canonAsociado, Poliza poliza) {
        this.direccionInmueble = direccionInmueble;
        this.canonAsociado = canonAsociado;
        this.poliza = poliza;
        this.estado = EstadoRiesgo.VIGENTE;
    }

    /** Marca el riesgo como cancelado. Operacion idempotente. */
    public void cancelar() {
        this.estado = EstadoRiesgo.CANCELADO;
    }

    public boolean estaCancelado() {
        return this.estado == EstadoRiesgo.CANCELADO;
    }

    public Long getId() {
        return id;
    }

    public String getDireccionInmueble() {
        return direccionInmueble;
    }

    public BigDecimal getCanonAsociado() {
        return canonAsociado;
    }

    public EstadoRiesgo getEstado() {
        return estado;
    }

    public Poliza getPoliza() {
        return poliza;
    }
}
