package co.com.segurosbolivar.polizaapi.domain;

/**
 * Tipo de poliza segun el negocio.
 * <ul>
 *   <li>INDIVIDUAL: el tomador y asegurado es el arrendatario; admite un unico riesgo.</li>
 *   <li>COLECTIVA: orientada a inmobiliarias/copropiedades; admite uno o muchos riesgos.</li>
 * </ul>
 */
public enum TipoPoliza {
    INDIVIDUAL,
    COLECTIVA
}
