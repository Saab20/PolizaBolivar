package co.com.segurosbolivar.polizaapi.repository;

import co.com.segurosbolivar.polizaapi.domain.EstadoPoliza;
import co.com.segurosbolivar.polizaapi.domain.Poliza;
import co.com.segurosbolivar.polizaapi.domain.TipoPoliza;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repositorio de polizas. Las consultas usan parametros vinculados (nunca
 * concatenacion de strings), evitando inyeccion SQL por diseno.
 */
public interface PolizaRepository extends JpaRepository<Poliza, Long> {

    /**
     * Lista polizas filtrando opcionalmente por tipo y estado.
     * Cuando un parametro es null, ese filtro no se aplica.
     */
    @Query("""
            SELECT p FROM Poliza p
            WHERE (:tipo IS NULL OR p.tipo = :tipo)
              AND (:estado IS NULL OR p.estado = :estado)
            """)
    List<Poliza> buscarPorTipoYEstado(@Param("tipo") TipoPoliza tipo,
                                      @Param("estado") EstadoPoliza estado);
}
