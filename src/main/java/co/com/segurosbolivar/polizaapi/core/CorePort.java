package co.com.segurosbolivar.polizaapi.core;

/**
 * Puerto de salida hacia el CORE legado de seguros (capa media WebLogic).
 * El dominio depende de esta abstraccion, no de su implementacion concreta,
 * de modo que el protocolo real (SOAP/REST) quede encapsulado en el adaptador.
 */
public interface CorePort {

    /**
     * Notifica al CORE una actualizacion de estado de una poliza o riesgo.
     *
     * @param evento   tipo de evento (p. ej. ACTUALIZACION)
     * @param polizaId identificador de la poliza afectada
     */
    void notificarEvento(String evento, Long polizaId);
}
