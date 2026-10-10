-- Datos de ejemplo para facilitar las pruebas de la API.
-- La prima = canon mensual * meses de vigencia.

-- Poliza individual (admite un unico riesgo).
INSERT INTO poliza (numero, tipo, estado, fecha_inicio_vigencia, fecha_fin_vigencia, meses_vigencia, canon_mensual, valor_prima, version)
VALUES ('IND-0001', 'INDIVIDUAL', 'VIGENTE', '2025-01-01', '2026-01-01', 12, 1500000.00, 18000000.00, 0);

-- Poliza colectiva (admite varios riesgos).
INSERT INTO poliza (numero, tipo, estado, fecha_inicio_vigencia, fecha_fin_vigencia, meses_vigencia, canon_mensual, valor_prima, version)
VALUES ('COL-0001', 'COLECTIVA', 'VIGENTE', '2025-02-01', '2026-02-01', 12, 2000000.00, 24000000.00, 0);

-- Riesgo de la poliza individual (id = 1).
INSERT INTO riesgo (direccion_inmueble, canon_asociado, estado, poliza_id)
VALUES ('Calle 100 # 15-20, Bogota', 1500000.00, 'VIGENTE', 1);

-- Riesgos de la poliza colectiva (id = 2).
INSERT INTO riesgo (direccion_inmueble, canon_asociado, estado, poliza_id)
VALUES ('Carrera 7 # 45-10, Bogota', 2000000.00, 'VIGENTE', 2);
INSERT INTO riesgo (direccion_inmueble, canon_asociado, estado, poliza_id)
VALUES ('Avenida 68 # 30-05, Bogota', 1800000.00, 'VIGENTE', 2);
