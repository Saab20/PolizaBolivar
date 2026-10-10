package co.com.segurosbolivar.polizaapi.controller;

import co.com.segurosbolivar.polizaapi.dto.RiesgoResponse;
import co.com.segurosbolivar.polizaapi.service.PolizaService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints de operaciones sobre riesgos individuales.
 */
@RestController
@RequestMapping("/api/v1/riesgos")
public class RiesgoController {

    private final PolizaService polizaService;

    public RiesgoController(PolizaService polizaService) {
        this.polizaService = polizaService;
    }

    /** POST /api/v1/riesgos/{id}/cancelar : cancela un riesgo puntual. */
    @PostMapping("/{id}/cancelar")
    public RiesgoResponse cancelar(@PathVariable Long id) {
        return polizaService.cancelarRiesgo(id);
    }
}
