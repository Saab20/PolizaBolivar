# API de Gestión de Pólizas

API REST para la gestión de pólizas de arrendamiento (individuales y colectivas), desarrollada como solución al **Módulo 2** de la prueba técnica para Desarrollador Senior TI de Seguros Bolívar.

## Stack técnico

- **Java 21**
- **Spring Boot 3.3.5** (Web, Data JPA, Validation)
- **H2** (base de datos en memoria, sin instalación)
- **Maven** (incluye Maven Wrapper, no requiere Maven instalado)
- **JUnit 5 + Spring Boot Test** para las pruebas

## Cómo ejecutar

Requisito único: tener **Java 21** disponible. No es necesario instalar Maven (el proyecto trae el wrapper).

```bash
# Clonar el repositorio
git clone <URL_DEL_REPOSITORIO>
cd poliza-api

# Ejecutar (Linux / macOS)
./mvnw spring-boot:run

# Ejecutar (Windows)
mvnw.cmd spring-boot:run
```

La API queda disponible en `http://localhost:8080`.

Alternativamente, se puede empaquetar y ejecutar el JAR:

```bash
./mvnw clean package
java -jar target/poliza-api-1.0.0.jar
```

Al arrancar se cargan datos de ejemplo (`src/main/resources/data.sql`): una póliza individual (id 1) y una colectiva (id 2) con sus riesgos.

## Seguridad

Todos los endpoints bajo `/api/v1/**` exigen el header:

```
x-api-key: 123456
```

Sin ese header, o con un valor incorrecto, la API responde `401 Unauthorized`. El endpoint `/core-mock/**` queda exento porque representa un sistema externo.

> La clave es configurable mediante la variable de entorno `API_KEY`. En un entorno real provendría de un gestor de secretos, no de texto plano.

## Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/v1/polizas` | Crea una póliza (individual o colectiva) |
| GET | `/api/v1/polizas?tipo=&estado=` | Lista pólizas, filtrando opcionalmente por tipo y estado |
| GET | `/api/v1/polizas/{id}/riesgos` | Lista los riesgos de una póliza |
| POST | `/api/v1/polizas/{id}/renovar` | Renueva: incrementa canon y prima por IPC y pasa a `RENOVADA` |
| POST | `/api/v1/polizas/{id}/cancelar` | Cancela la póliza y todos sus riesgos |
| POST | `/api/v1/polizas/{id}/riesgos` | Agrega un riesgo (solo pólizas colectivas) |
| POST | `/api/v1/riesgos/{id}/cancelar` | Cancela un riesgo puntual |
| POST | `/core-mock/evento` | Mock del CORE legado: registra el evento en logs |

Valores válidos:
- `tipo`: `INDIVIDUAL`, `COLECTIVA`
- `estado`: `VIGENTE`, `RENOVADA`, `CANCELADA`

## Reglas de negocio implementadas

1. Una póliza **individual** solo puede tener **un** riesgo.
2. **No** se puede renovar una póliza **cancelada**.
3. La cancelación de una póliza **cancela todos** sus riesgos.
4. Agregar un riesgo exige que la póliza sea **colectiva** (valida el tipo).

Reglas de cálculo:
- `prima = canon mensual × meses de vigencia`.
- Al renovar: `canon = canon × (1 + IPC)` y se recalcula la prima. El IPC por defecto es `0.0509` (5.09%), configurable con la variable de entorno `IPC`.

## Ejemplos de uso (cURL)

```bash
API=http://localhost:8080
KEY="x-api-key: 123456"

# Listar todas las pólizas
curl -H "$KEY" "$API/api/v1/polizas"

# Filtrar colectivas vigentes
curl -H "$KEY" "$API/api/v1/polizas?tipo=COLECTIVA&estado=VIGENTE"

# Ver los riesgos de la póliza 2
curl -H "$KEY" "$API/api/v1/polizas/2/riesgos"

# Renovar la póliza 2
curl -X POST -H "$KEY" "$API/api/v1/polizas/2/renovar"

# Agregar un riesgo a una colectiva
curl -X POST -H "$KEY" -H "Content-Type: application/json" \
  -d '{"direccionInmueble":"Calle 100 # 15-20","canonAsociado":1500000}' \
  "$API/api/v1/polizas/2/riesgos"

# Cancelar la póliza 2 (cancela también sus riesgos)
curl -X POST -H "$KEY" "$API/api/v1/polizas/2/cancelar"

# Cancelar un riesgo puntual
curl -X POST -H "$KEY" "$API/api/v1/riesgos/1/cancelar"

# Notificar un evento al mock del CORE (no requiere x-api-key)
curl -X POST -H "Content-Type: application/json" \
  -d '{"evento":"ACTUALIZACION","polizaId":555}' \
  "$API/core-mock/evento"
```

## Estructura del proyecto

```
src/main/java/co/com/segurosbolivar/polizaapi/
├── PolizaApiApplication.java        # Arranque
├── controller/                      # Capa de presentación (REST)
│   ├── PolizaController.java
│   ├── RiesgoController.java
│   ├── CoreMockController.java
│   └── ManejadorGlobalExcepciones.java
├── service/                         # Capa de lógica / orquestación
│   └── PolizaService.java
├── repository/                      # Capa de persistencia (Spring Data JPA)
│   ├── PolizaRepository.java
│   └── RiesgoRepository.java
├── domain/                          # Entidades y reglas de negocio
│   ├── Poliza.java
│   ├── Riesgo.java
│   ├── TipoPoliza.java / EstadoPoliza.java / EstadoRiesgo.java
│   └── exception/
├── dto/                             # Objetos de entrada/salida (contratos)
├── core/                            # Puerto + adaptador del CORE legado
│   ├── CorePort.java
│   └── CoreMockAdapter.java
└── security/
    └── ApiKeyFilter.java            # Validación del header x-api-key
```

La organización sigue una separación por capas (controller → service → repository) con el dominio en el centro: las reglas de negocio viven en las entidades `Poliza` y `Riesgo`, y la integración con el CORE se consume a través de un **puerto** (`CorePort`), de modo que su implementación real sea intercambiable.

## Pruebas

```bash
./mvnw test
```

Incluye pruebas unitarias de las reglas de negocio del dominio y pruebas de integración de la API (seguridad por `x-api-key`, filtros, renovación y validaciones).

## Decisiones de diseño

- **Reglas en el dominio:** las invariantes (cardinalidad de riesgos, no renovar cancelada, cascada de cancelación) se encapsulan en la entidad `Poliza`, no en el servicio ni en la base de datos. Esto las hace testeables sin infraestructura.
- **Puerto hacia el CORE:** `CorePort` abstrae la integración con la capa media WebLogic; `CoreMockAdapter` es la implementación de prueba que registra el intento en logs.
- **DTOs de salida:** nunca se exponen las entidades JPA directamente; el mapeo se hace dentro de la transacción para no depender de `open-in-view`.
- **Consultas parametrizadas:** el filtrado se hace con parámetros vinculados, evitando concatenación de SQL.
- **Manejo de errores centralizado:** un `@RestControllerAdvice` traduce las excepciones a respuestas HTTP consistentes (400/404/409) sin exponer trazas internas.
