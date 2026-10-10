package co.com.segurosbolivar.polizaapi.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Adaptador que simula la integracion con el CORE legado.
 * En lugar de invocar la capa media WebLogic real, registra en logs que la
 * operacion se intento enviar al CORE, tal como pide el enunciado.
 */
@Component
public class CoreMockAdapter implements CorePort {

    private static final Logger log = LoggerFactory.getLogger(CoreMockAdapter.class);

    @Override
    public void notificarEvento(String evento, Long polizaId) {
        log.info("[CORE-MOCK] Enviando evento al CORE legado -> evento={}, polizaId={}",
                evento, polizaId);
    }
}
