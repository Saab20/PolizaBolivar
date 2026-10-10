package co.com.segurosbolivar.polizaapi.controller;

import co.com.segurosbolivar.polizaapi.domain.EstadoPoliza;
import co.com.segurosbolivar.polizaapi.domain.TipoPoliza;
import co.com.segurosbolivar.polizaapi.dto.AgregarRiesgoRequest;
import co.com.segurosbolivar.polizaapi.dto.CrearPolizaRequest;
import co.com.segurosbolivar.polizaapi.dto.PolizaResponse;
import co.com.segurosbolivar.polizaapi.dto.RiesgoResponse;
import co.com.segurosbolivar.polizaapi.service.PolizaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints de gestion de polizas y sus riesgos.
 * Versionado por prefijo de ruta segun el estandar de la organizacion.
 */
@RestController
@RequestMapping("/api/v1/polizas")
public class PolizaController {

    private final PolizaService polizaService;

    public PolizaController(PolizaService polizaService) {
        this.polizaService = polizaService;
    }

    /** POST /api/v1/polizas : crea una poliza (individual o colectiva). */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PolizaResponse crear(@Valid @RequestBody CrearPolizaRequest request) {
        return polizaService.crear(request);
    }

    /** GET /api/v1/polizas?tipo=&estado= : lista polizas filtrando por tipo y estado. */
    @GetMapping
    public List<PolizaResponse> listar(
            @RequestParam(required = false) TipoPoliza tipo,
            @RequestParam(required = false) EstadoPoliza estado) {
        return polizaService.listar(tipo, estado);
    }

    /** GET /api/v1/polizas/{id}/riesgos : lista los riesgos de una poliza. */
    @GetMapping("/{id}/riesgos")
    public List<RiesgoResponse> listarRiesgos(@PathVariable Long id) {
        return polizaService.listarRiesgos(id);
    }

    /** POST /api/v1/polizas/{id}/renovar : renueva incrementando canon y prima por IPC. */
    @PostMapping("/{id}/renovar")
    public PolizaResponse renovar(@PathVariable Long id) {
        return polizaService.renovar(id);
    }

    /** POST /api/v1/polizas/{id}/cancelar : cancela la poliza y todos sus riesgos. */
    @PostMapping("/{id}/cancelar")
    public PolizaResponse cancelar(@PathVariable Long id) {
        return polizaService.cancelar(id);
    }

    /** POST /api/v1/polizas/{id}/riesgos : agrega un riesgo (solo polizas colectivas). */
    @PostMapping("/{id}/riesgos")
    @ResponseStatus(HttpStatus.CREATED)
    public RiesgoResponse agregarRiesgo(@PathVariable Long id,
                                        @Valid @RequestBody AgregarRiesgoRequest request) {
        return polizaService.agregarRiesgo(id, request);
    }
}
