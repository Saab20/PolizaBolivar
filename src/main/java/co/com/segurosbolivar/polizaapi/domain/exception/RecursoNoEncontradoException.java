package co.com.segurosbolivar.polizaapi.domain.exception;

/**
 * Se lanza cuando no existe el recurso solicitado (poliza o riesgo).
 * Se traduce a HTTP 404 Not Found en la capa de presentacion.
 */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
