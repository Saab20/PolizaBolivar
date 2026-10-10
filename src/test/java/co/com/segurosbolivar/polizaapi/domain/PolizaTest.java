package co.com.segurosbolivar.polizaapi.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import co.com.segurosbolivar.polizaapi.domain.exception.ReglaNegocioException;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de las reglas de negocio encapsuladas en el dominio Poliza.
 */
class PolizaTest {

    private static final BigDecimal IPC = new BigDecimal("0.0509");

    private Poliza polizaIndividual() {
        return new Poliza("IND-1", TipoPoliza.INDIVIDUAL, LocalDate.of(2025, 1, 1),
                12, new BigDecimal("1000000"));
    }

    private Poliza polizaColectiva() {
        return new Poliza("COL-1", TipoPoliza.COLECTIVA, LocalDate.of(2025, 1, 1),
                12, new BigDecimal("1000000"));
    }

    @Test
    @DisplayName("La prima se calcula como canon mensual por meses de vigencia")
    void calculaPrimaAlCrear() {
        Poliza poliza = polizaIndividual();
        assertThat(poliza.getValorPrima()).isEqualByComparingTo("12000000");
        assertThat(poliza.getEstado()).isEqualTo(EstadoPoliza.VIGENTE);
    }

    @Test
    @DisplayName("Renovar incrementa canon y prima por IPC y pasa a RENOVADA")
    void renuevaConIpc() {
        Poliza poliza = polizaColectiva();
        poliza.renovar(IPC);
        assertThat(poliza.getCanonMensual()).isEqualByComparingTo("1050900.00");
        assertThat(poliza.getValorPrima()).isEqualByComparingTo("12610800.00");
        assertThat(poliza.getEstado()).isEqualTo(EstadoPoliza.RENOVADA);
    }

    @Test
    @DisplayName("No se puede renovar una poliza cancelada")
    void noRenuevaCancelada() {
        Poliza poliza = polizaIndividual();
        poliza.cancelar();
        assertThatThrownBy(() -> poliza.renovar(IPC))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("cancelada");
    }

    @Test
    @DisplayName("Una poliza individual solo admite un riesgo")
    void individualSoloUnRiesgo() {
        Poliza poliza = polizaIndividual();
        poliza.agregarRiesgo("Calle 1", new BigDecimal("1000000"));
        assertThatThrownBy(() -> poliza.agregarRiesgo("Calle 2", new BigDecimal("900000")))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("individual");
    }

    @Test
    @DisplayName("Una poliza colectiva admite varios riesgos")
    void colectivaVariosRiesgos() {
        Poliza poliza = polizaColectiva();
        poliza.agregarRiesgo("Calle 1", new BigDecimal("1000000"));
        poliza.agregarRiesgo("Calle 2", new BigDecimal("900000"));
        assertThat(poliza.getRiesgos()).hasSize(2);
    }

    @Test
    @DisplayName("Cancelar la poliza cancela todos sus riesgos")
    void cancelarCancelaRiesgos() {
        Poliza poliza = polizaColectiva();
        poliza.agregarRiesgo("Calle 1", new BigDecimal("1000000"));
        poliza.agregarRiesgo("Calle 2", new BigDecimal("900000"));
        poliza.cancelar();
        assertThat(poliza.getEstado()).isEqualTo(EstadoPoliza.CANCELADA);
        assertThat(poliza.getRiesgos()).allMatch(Riesgo::estaCancelado);
    }
}
