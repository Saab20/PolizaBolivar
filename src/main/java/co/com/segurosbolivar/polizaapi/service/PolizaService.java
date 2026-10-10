package co.com.segurosbolivar.polizaapi.service;

import co.com.segurosbolivar.polizaapi.core.CorePort;
import co.com.segurosbolivar.polizaapi.domain.EstadoPoliza;
import co.com.segurosbolivar.polizaapi.domain.Poliza;
import co.com.segurosbolivar.polizaapi.domain.Riesgo;
import co.com.segurosbolivar.polizaapi.domain.TipoPoliza;
import co.com.segurosbolivar.polizaapi.domain.exception.RecursoNoEncontradoException;
import co.com.segurosbolivar.polizaapi.dto.AgregarRiesgoRequest;
import co.com.segurosbolivar.polizaapi.dto.CrearPolizaRequest;
import co.com.segurosbolivar.polizaapi.dto.PolizaResponse;
import co.com.segurosbolivar.polizaapi.dto.RiesgoResponse;
import co.com.segurosbolivar.polizaapi.repository.PolizaRepository;
import co.com.segurosbolivar.polizaapi.repository.RiesgoRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Orquesta los casos de uso de polizas y riesgos. Las reglas de negocio viven
 * en el dominio (Poliza/Riesgo); este servicio coordina persistencia, el ajuste
 * por IPC y la notificacion al CORE legado.
 *
 * El mapeo a DTO se hace dentro de la transaccion para no depender de
 * open-in-view (desactivado) y evitar LazyInitializationException.
 */
@Service
public class PolizaService {

    private static final String EVENTO_ACTUALIZACION = "ACTUALIZACION";

    private final PolizaRepository polizaRepository;
    private final RiesgoRepository riesgoRepository;
    private final CorePort corePort;
    private final BigDecimal ipc;

    public PolizaService(PolizaRepository polizaRepository,
                         RiesgoRepository riesgoRepository,
                         CorePort corePort,
                         @Value("${app.renovacion.ipc}") BigDecimal ipc) {
        this.polizaRepository = polizaRepository;
        this.riesgoRepository = riesgoRepository;
        this.corePort = corePort;
        this.ipc = ipc;
    }

    /** Lista polizas filtrando opcionalmente por tipo y estado. */
    @Transactional(readOnly = true)
    public List<PolizaResponse> listar(TipoPoliza tipo, EstadoPoliza estado) {
        return polizaRepository.buscarPorTipoYEstado(tipo, estado).stream()
                .map(PolizaResponse::desde)
                .toList();
    }

    /** Crea una poliza nueva. Toda alta notifica al CORE. */
    @Transactional
    public PolizaResponse crear(CrearPolizaRequest request) {
        Poliza poliza = new Poliza(
                request.numero(),
                request.tipo(),
                request.fechaInicioVigencia(),
                request.mesesVigencia(),
                request.canonMensual());
        Poliza guardada = polizaRepository.save(poliza);
        corePort.notificarEvento(EVENTO_ACTUALIZACION, guardada.getId());
        return PolizaResponse.desde(guardada);
    }

    /** Devuelve los riesgos de una poliza, validando que exista. */
    @Transactional(readOnly = true)
    public List<RiesgoResponse> listarRiesgos(Long polizaId) {
        obtenerPoliza(polizaId);
        return riesgoRepository.findByPolizaId(polizaId).stream()
                .map(RiesgoResponse::desde)
                .toList();
    }

    /** Renueva la poliza (ajuste de canon y prima por IPC) y notifica al CORE. */
    @Transactional
    public PolizaResponse renovar(Long polizaId) {
        Poliza poliza = obtenerPoliza(polizaId);
        poliza.renovar(ipc);
        corePort.notificarEvento(EVENTO_ACTUALIZACION, poliza.getId());
        return PolizaResponse.desde(poliza);
    }

    /** Cancela la poliza y, en cascada, todos sus riesgos; notifica al CORE. */
    @Transactional
    public PolizaResponse cancelar(Long polizaId) {
        Poliza poliza = obtenerPoliza(polizaId);
        poliza.cancelar();
        corePort.notificarEvento(EVENTO_ACTUALIZACION, poliza.getId());
        return PolizaResponse.desde(poliza);
    }

    /** Agrega un riesgo a la poliza (el dominio valida el tipo) y notifica al CORE. */
    @Transactional
    public RiesgoResponse agregarRiesgo(Long polizaId, AgregarRiesgoRequest request) {
        Poliza poliza = obtenerPoliza(polizaId);
        Riesgo riesgo = poliza.agregarRiesgo(request.direccionInmueble(), request.canonAsociado());
        // Flush para que se genere el identificador del nuevo riesgo antes de mapear la respuesta.
        polizaRepository.saveAndFlush(poliza);
        corePort.notificarEvento(EVENTO_ACTUALIZACION, poliza.getId());
        return RiesgoResponse.desde(riesgo);
    }

    /** Cancela un riesgo individual y notifica al CORE. */
    @Transactional
    public RiesgoResponse cancelarRiesgo(Long riesgoId) {
        Riesgo riesgo = riesgoRepository.findById(riesgoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el riesgo con id " + riesgoId));
        riesgo.cancelar();
        corePort.notificarEvento(EVENTO_ACTUALIZACION, riesgo.getPoliza().getId());
        return RiesgoResponse.desde(riesgo);
    }

    private Poliza obtenerPoliza(Long polizaId) {
        return polizaRepository.findById(polizaId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe la poliza con id " + polizaId));
    }
}
