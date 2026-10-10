package co.com.segurosbolivar.polizaapi.dto;

import co.com.segurosbolivar.polizaapi.domain.EstadoRiesgo;
import co.com.segurosbolivar.polizaapi.domain.Riesgo;
import java.math.BigDecimal;

/**
 * Representacion de salida de un riesgo. Expone solo los campos necesarios.
 */
public record RiesgoResponse(
        Long id,
        String direccionInmueble,
        BigDecimal canonAsociado,
        EstadoRiesgo estado
) {
    public static RiesgoResponse desde(Riesgo riesgo) {
        return new RiesgoResponse(
                riesgo.getId(),
                riesgo.getDireccionInmueble(),
                riesgo.getCanonAsociado(),
                riesgo.getEstado());
    }
}
