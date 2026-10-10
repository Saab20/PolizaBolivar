package co.com.segurosbolivar.polizaapi.domain;

/**
 * Tipo de poliza segun el negocio.
 * 
 *   INDIVIDUAL: el tomador y asegurado es el arrendatario; admite un unico riesgo.
 *   COLECTIVA: orientada a inmobiliarias/copropiedades; admite uno o muchos riesgos.
 * 
 */
public enum TipoPoliza {
    INDIVIDUAL,
    COLECTIVA
}
