package co.com.segurosbolivar.polizaapi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Pruebas de integracion de la API: seguridad por api-key y casos de uso clave
 * sobre los datos de ejemplo cargados por data.sql.
 */
@SpringBootTest
@AutoConfigureMockMvc
class PolizaControllerIntegrationTest {

    private static final String API_KEY = "x-api-key";
    private static final String CLAVE = "123456";

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Sin x-api-key responde 401")
    void sinApiKeyNoAutorizado() throws Exception {
        mockMvc.perform(get("/api/v1/polizas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Con x-api-key correcta lista las polizas")
    void listaConApiKey() throws Exception {
        mockMvc.perform(get("/api/v1/polizas").header(API_KEY, CLAVE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("Filtra polizas por tipo")
    void filtraPorTipo() throws Exception {
        mockMvc.perform(get("/api/v1/polizas").param("tipo", "COLECTIVA").header(API_KEY, CLAVE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].tipo").value("COLECTIVA"));
    }

    @Test
    @DisplayName("Agregar un segundo riesgo a una individual responde 409")
    void segundoRiesgoIndividualConflicto() throws Exception {
        String body = "{\"direccionInmueble\":\"Calle 2\",\"canonAsociado\":900000}";
        mockMvc.perform(post("/api/v1/polizas/1/riesgos")
                        .header(API_KEY, CLAVE)
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Renovar aplica el IPC y cambia el estado a RENOVADA")
    void renuevaPoliza() throws Exception {
        mockMvc.perform(post("/api/v1/polizas/2/renovar").header(API_KEY, CLAVE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("RENOVADA"))
                .andExpect(jsonPath("$.canonMensual").value(2101800.00));
    }
}
