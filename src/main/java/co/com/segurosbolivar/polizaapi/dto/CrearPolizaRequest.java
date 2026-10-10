package co.com.segurosbolivar.polizaapi.dto;

import co.com.segurosbolivar.polizaapi.domain.TipoPoliza;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Datos de entrada para crear una poliza. Toda validacion de formato,
 * obligatoriedad y rango se declara aqui y se aplica en el servidor.
 */
public record CrearPolizaRequest(

        @NotBlank(message = "El numero de poliza es obligatorio.")
        String numero,

        @NotNull(message = "El tipo de poliza es obligatorio (INDIVIDUAL o COLECTIVA).")
        TipoPoliza tipo,

        @NotNull(message = "La fecha de inicio de vigencia es obligatoria.")
        LocalDate fechaInicioVigencia,

        @Min(value = 1, message = "Los meses de vigencia deben ser al menos 1.")
        int mesesVigencia,

        @NotNull(message = "El canon mensual es obligatorio.")
        @DecimalMin(value = "0.0", inclusive = false, message = "El canon mensual debe ser mayor a cero.")
        BigDecimal canonMensual
) {
}
