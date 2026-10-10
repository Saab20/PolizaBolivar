package co.com.segurosbolivar.polizaapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Payload recibido por el endpoint mock del CORE.
 * Ejemplo: { "evento": "ACTUALIZACION", "polizaId": 555 }
 */
public record EventoCoreRequest(

        @NotBlank(message = "El campo 'evento' es obligatorio.")
        String evento,

        @NotNull(message = "El campo 'polizaId' es obligatorio.")
        Long polizaId
) {
}
