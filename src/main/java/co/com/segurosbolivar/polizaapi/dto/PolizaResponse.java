package co.com.segurosbolivar.polizaapi.dto;

import co.com.segurosbolivar.polizaapi.domain.EstadoPoliza;
import co.com.segurosbolivar.polizaapi.domain.Poliza;
import co.com.segurosbolivar.polizaapi.domain.TipoPoliza;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Representacion de salida de una poliza con sus riesgos.
 */
public record PolizaResponse(
        Long id,
        String numero,
        TipoPoliza tipo,
        EstadoPoliza estado,
        LocalDate fechaInicioVigencia,
        LocalDate fechaFinVigencia,
        int mesesVigencia,
        BigDecimal canonMensual,
        BigDecimal valorPrima,
        List<RiesgoResponse> riesgos
) {
    public static PolizaResponse desde(Poliza poliza) {
        List<RiesgoResponse> riesgos = poliza.getRiesgos().stream()
                .map(RiesgoResponse::desde)
                .toList();
        return new PolizaResponse(
                poliza.getId(),
                poliza.getNumero(),
                poliza.getTipo(),
                poliza.getEstado(),
                poliza.getFechaInicioVigencia(),
                poliza.getFechaFinVigencia(),
                poliza.getMesesVigencia(),
                poliza.getCanonMensual(),
                poliza.getValorPrima(),
                riesgos);
    }
}
