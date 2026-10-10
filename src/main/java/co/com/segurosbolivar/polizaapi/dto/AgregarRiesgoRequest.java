package co.com.segurosbolivar.polizaapi.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Datos de entrada para agregar un riesgo a una poliza colectiva.
 */
public record AgregarRiesgoRequest(

        @NotBlank(message = "La direccion del inmueble es obligatoria.")
        String direccionInmueble,

        @NotNull(message = "El canon asociado es obligatorio.")
        @DecimalMin(value = "0.0", inclusive = false, message = "El canon asociado debe ser mayor a cero.")
        BigDecimal canonAsociado
) {
}
