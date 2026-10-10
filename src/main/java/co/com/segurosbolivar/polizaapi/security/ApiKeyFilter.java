package co.com.segurosbolivar.polizaapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filtro transversal que exige el header 'x-api-key' en todas las peticiones a la API.
 * Rechaza con 401 cuando el header falta o no coincide con el valor configurado.
 *
 * Nota de seguridad: una api-key estatica es el minimo que pide el enunciado.
 * En produccion se sustituiria por JWT firmado por el IdP institucional, validado
 * en el API Gateway y re-verificado aqui por defensa en profundidad.
 */
@Component
@Order(1)
public class ApiKeyFilter extends OncePerRequestFilter {

    private static final String HEADER = "x-api-key";

    private final byte[] apiKeyEsperada;

    public ApiKeyFilter(@Value("${app.security.api-key}") String apiKey) {
        this.apiKeyEsperada = apiKey.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String apiKeyRecibida = request.getHeader(HEADER);

        if (apiKeyRecibida == null || !esClaveValida(apiKeyRecibida)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(
                    "{\"estado\":401,\"mensaje\":\"x-api-key invalida o ausente.\"}");
            return;
        }
        filterChain.doFilter(request, response);
    }

    /** Comparacion en tiempo constante para no filtrar informacion por timing. */
    private boolean esClaveValida(String recibida) {
        return MessageDigest.isEqual(recibida.getBytes(StandardCharsets.UTF_8), apiKeyEsperada);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // El mock del CORE simula un sistema externo y queda fuera del control de api-key.
        return path.startsWith("/core-mock");
    }
}
