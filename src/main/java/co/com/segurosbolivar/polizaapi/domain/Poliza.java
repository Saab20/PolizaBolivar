package co.com.segurosbolivar.polizaapi.domain;

import co.com.segurosbolivar.polizaapi.domain.exception.ReglaNegocioException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Poliza de arrendamiento. Es la raiz de agregado: concentra las reglas de
 * negocio que afectan su propio estado y el de sus riesgos, de modo que las
 * invariantes del dominio no dependan de la capa de servicio ni de la base de datos.
 */
@Entity
@Table(name = "poliza")
public class Poliza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String numero;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoPoliza tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPoliza estado;

    @Column(nullable = false)
    private LocalDate fechaInicioVigencia;

    @Column(nullable = false)
    private LocalDate fechaFinVigencia;

    /** Numero de meses del periodo de vigencia. */
    @Column(nullable = false)
    private int mesesVigencia;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal canonMensual;

    /** Prima = canon mensual * meses de vigencia. Se persiste para trazabilidad historica. */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valorPrima;

    /** Bloqueo optimista para evitar actualizaciones concurrentes perdidas. */
    @Version
    private Long version;

    @OneToMany(mappedBy = "poliza", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Riesgo> riesgos = new ArrayList<>();

    protected Poliza() {
        // Requerido por JPA.
    }

    public Poliza(String numero, TipoPoliza tipo, LocalDate fechaInicioVigencia,
                  int mesesVigencia, BigDecimal canonMensual) {
        if (mesesVigencia <= 0) {
            throw new ReglaNegocioException("Los meses de vigencia deben ser mayores a cero.");
        }
        this.numero = numero;
        this.tipo = tipo;
        this.estado = EstadoPoliza.VIGENTE;
        this.fechaInicioVigencia = fechaInicioVigencia;
        this.mesesVigencia = mesesVigencia;
        this.fechaFinVigencia = fechaInicioVigencia.plusMonths(mesesVigencia);
        this.canonMensual = canonMensual;
        this.valorPrima = calcularPrima(canonMensual, mesesVigencia);
    }

    /**
     * Renueva la poliza por el mismo periodo de vigencia, ajustando el canon
     * segun el incremento del IPC y recalculando la prima.
     * Regla: no se puede renovar una poliza cancelada.
     *
     * @param ipc factor de incremento (ej. 0.0509 para 5.09%)
     */
    public void renovar(BigDecimal ipc) {
        if (this.estado == EstadoPoliza.CANCELADA) {
            throw new ReglaNegocioException("No se puede renovar una poliza cancelada.");
        }
        BigDecimal factor = BigDecimal.ONE.add(ipc);
        this.canonMensual = this.canonMensual.multiply(factor).setScale(2, RoundingMode.HALF_UP);
        this.fechaInicioVigencia = this.fechaFinVigencia;
        this.fechaFinVigencia = this.fechaInicioVigencia.plusMonths(this.mesesVigencia);
        this.valorPrima = calcularPrima(this.canonMensual, this.mesesVigencia);
        this.estado = EstadoPoliza.RENOVADA;
    }

    /**
     * Cancela la poliza y, en cascada, todos sus riesgos.
     * Regla: la cancelacion de una poliza cancela todos sus riesgos.
     */
    public void cancelar() {
        this.estado = EstadoPoliza.CANCELADA;
        this.riesgos.forEach(Riesgo::cancelar);
    }

    /**
     * Agrega un riesgo validando las reglas de cardinalidad por tipo de poliza:
     * una individual admite un unico riesgo; una colectiva, varios.
     * No se agregan riesgos a una poliza cancelada.
     */
    public Riesgo agregarRiesgo(String direccionInmueble, BigDecimal canonAsociado) {
        if (this.estado == EstadoPoliza.CANCELADA) {
            throw new ReglaNegocioException("No se pueden agregar riesgos a una poliza cancelada.");
        }
        if (this.tipo == TipoPoliza.INDIVIDUAL && contarRiesgosVigentes() >= 1) {
            throw new ReglaNegocioException(
                    "Una poliza individual solo puede tener un riesgo.");
        }
        Riesgo riesgo = new Riesgo(direccionInmueble, canonAsociado, this);
        this.riesgos.add(riesgo);
        return riesgo;
    }

    private long contarRiesgosVigentes() {
        return this.riesgos.stream().filter(r -> !r.estaCancelado()).count();
    }

    private static BigDecimal calcularPrima(BigDecimal canonMensual, int mesesVigencia) {
        return canonMensual.multiply(BigDecimal.valueOf(mesesVigencia))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public Long getId() {
        return id;
    }

    public String getNumero() {
        return numero;
    }

    public TipoPoliza getTipo() {
        return tipo;
    }

    public EstadoPoliza getEstado() {
        return estado;
    }

    public LocalDate getFechaInicioVigencia() {
        return fechaInicioVigencia;
    }

    public LocalDate getFechaFinVigencia() {
        return fechaFinVigencia;
    }

    public int getMesesVigencia() {
        return mesesVigencia;
    }

    public BigDecimal getCanonMensual() {
        return canonMensual;
    }

    public BigDecimal getValorPrima() {
        return valorPrima;
    }

    public List<Riesgo> getRiesgos() {
        return Collections.unmodifiableList(riesgos);
    }
}
