package co.com.segurosbolivar.polizaapi.controller;

import co.com.segurosbolivar.polizaapi.core.CorePort;
import co.com.segurosbolivar.polizaapi.dto.EventoCoreRequest;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Mock del CORE legado exigido por el enunciado. Su unico proposito es registrar
 * en logs que la operacion se intento enviar al CORE. No requiere api-key porque
 * representa un sistema externo distinto a la API de polizas.
 */
@RestController
@RequestMapping("/core-mock")
public class CoreMockController {

    private final CorePort corePort;

    public CoreMockController(CorePort corePort) {
        this.corePort = corePort;
    }

    /** POST /core-mock/evento : recibe un evento y lo registra en logs. */
    @PostMapping("/evento")
    public Map<String, Object> recibirEvento(@Valid @RequestBody EventoCoreRequest request) {
        corePort.notificarEvento(request.evento(), request.polizaId());
        return Map.of(
                "recibido", true,
                "evento", request.evento(),
                "polizaId", request.polizaId());
    }
}
