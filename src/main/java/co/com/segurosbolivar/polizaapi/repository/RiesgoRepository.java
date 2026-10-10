package co.com.segurosbolivar.polizaapi.repository;

import co.com.segurosbolivar.polizaapi.domain.Riesgo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio de riesgos.
 */
public interface RiesgoRepository extends JpaRepository<Riesgo, Long> {

    /** Lista los riesgos de una poliza dada. */
    List<Riesgo> findByPolizaId(Long polizaId);
}
