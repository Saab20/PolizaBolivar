package co.com.segurosbolivar.polizaapi.domain.exception;

/**
 * Se lanza cuando una operacion viola una regla de negocio del dominio
 * (p. ej. renovar una poliza cancelada o agregar un segundo riesgo a una individual).
 * Se traduce a HTTP 409 Conflict en la capa de presentacion.
 */
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
