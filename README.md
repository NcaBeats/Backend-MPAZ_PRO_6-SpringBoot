# MPAZ PRO 6° - Sistema de Gestión Pedagógica (Spring Boot)

## Índice

- [1. Descripción del proyecto](#1-descripción-del-proyecto)
- [2. Objetivos](#2-objetivos)
- [3. Alcance](#3-alcance)
- [4. Decisión de modelado: ¿Por qué el modelo definitivo?](#4-decisión-de-modelado-por-qué-el-modelo-definitivo)
- [5. Arquitectura del sistema](#5-arquitectura-del-sistema)
- [6. Modelo de datos](#6-modelo-de-datos)
- [7. Stack tecnológico](#7-stack-tecnológico)
- [8. Requisitos previos](#8-requisitos-previos)
- [9. Instalación y configuración](#9-instalación-y-configuración)
- [10. Ejecución de la aplicación](#10-ejecución-de-la-aplicación)
- [11. Estructura del proyecto](#11-estructura-del-proyecto)
- [12. Migraciones Flyway](#12-migraciones-flyway)
- [13. Convenciones de desarrollo](#13-convenciones-de-desarrollo)
- [14. Datos de prueba (Seed)](#14-datos-de-prueba-seed)
- [15. Pruebas](#15-pruebas)
- [16. Flujo de trabajo con Git](#16-flujo-de-trabajo-con-git)
- [17. Solución de problemas comunes](#17-solución-de-problemas-comunes)
- [18. Referencias](#18-referencias)
- [19. Créditos y contribución](#19-créditos-y-contribución)
- [20. Licencia](#20-licencia)

## 1. Descripción del proyecto

MPAZ PRO 6° es un sistema de gestión pedagógica desarrollado con Spring Boot, orientado a la planificación, asignación y seguimiento de actividades educativas para 6° básico.

El sistema permite gestionar usuarios (UTP, Docente, Estudiante), asignaturas, unidades, objetivos de aprendizaje, contenidos, recursos, actividades (incluida la Prueba Final), asignaciones docentes, intentos de resolución, respuestas de estudiantes y la generación de un informe pedagógico consolidado por estudiante y unidad.

## 2. Objetivos

- Gestionar el catálogo curricular (Asignaturas, Unidades, OA, Contenidos, Recursos).
- Controlar la autorización de unidades por parte de la UTP.
- Permitir al docente asignar OA, actividades/desafíos y habilitar la Prueba Final con límite de intentos.
- Registrar los intentos y respuestas del estudiante.
- Generar un informe pedagógico consolidado por estudiante y unidad, con fortalezas y refuerzos por OA.
- Garantizar integridad referencial entre unidades mediante restricciones a nivel de base de datos.
- Mantener separación entre lo que se calcula (derivado) y lo que se congela (snapshot del informe).

## 3. Alcance

- Rol UTP: autoriza/revisa unidades según estado.
- Rol Docente: gestiona asignaciones, valida informe pedagógico.
- Rol Estudiante: resuelve actividades asignadas, visualiza contenido autorizado, accede solo a sus propios datos.
- Informe consolidado por estudiante y unidad. Se toma el último intento completado por actividad.
- Asignación sin filas (asignacion_oa / asignacion_actividad) implica que no hay contenido/actividades visibles para el estudiante.
- Sin IA ni diagnóstico automatizado.
- Datos 100% ficticios para fines académicos.

## 4. Decisión de modelado: ¿Por qué el modelo definitivo?

La especificación adoptada como fuente autoritativa es `especificaciones/mpaz_pro_modelo_definitivo.md`. Esta versión recoge las decisiones necesarias para cubrir ambigüedades detectadas durante la implementación, sin descartar cambios futuros si se detectan huecos.

### 4.1. Evolución del modelado

- **1-modelado.md**: Primera aproximación. Presentaba ambigüedad sobre el informe y la trazabilidad entre unidades.
- **mpaz_pro_v4_lean.md**: Versión intermedia con intención de simplificar. Útil, pero incompleto para garantizar integridad.
- **mpaz_pro_modelo_definitivo.md**: Versión definitiva. Consolida las 12 decisiones tomadas (con resolución y cómo revertir cada una), define qué queda en BD vs código, y alinea modelo con DTOs/mappers/entidades/migraciones.

### 4.2. Razones para adoptar el modelo definitivo

- Las decisiones tomadas están documentadas para facilitar su comprensión.
- 7 FK compuestas para ayudar a mantener la coherencia entre unidades.
- CHECKs para estados, fechas, rangos y coherencia VALIDADO↔fecha_validacion.
- Índice parcial para garantizar una sola PRUEBA_FINAL por unidad.
- Diferenciación entre derivado (calculado) y congelado (snapshot del informe).
- Informe consolidado por estudiante y unidad; último intento completado.
- Reglas delegadas al código cuando la BD no puede expresarlas (rol/dueño, trazabilidad pregunta→intento, etc.).
- Alineado a Spring Boot + JPA + Flyway + MapStruct + Bean Validation.

### 4.3. Modelo sujeto a ajustes

Este modelo responde a lo planteado en el caso de estudio. No se considera definitivo de forma rígida: si en su revisión o desarrollo se detectan huecos, ambigüedades o cambios solicitados, puede ajustarse. En ese caso, cualquier modificación debe documentarse en la especificación y aplicarse mediante migraciones Flyway.

## 5. Arquitectura del sistema

### 5.1. Principios

- Feature-based packaging (identidad, curriculo, docencia, evaluacion, informe, common)
- Separación por capa (model/repository/dto/mapper)
- Relaciones resueltas en Servicio vía getReferenceById
- DTOs record con validación; entidades no expuestas
- Flyway como fuente de verdad; ddl-auto: validate

### 5.2. Estructura de paquetes

```
src/main/java/com/example/mpaz_pro_6springboot/
├── common/enums/
├── identidad/
├── curriculo/
├── docencia/
├── evaluacion/
├── informe/
└── MpazPro6SpringBootApplication.java
```

### 5.3. Convenciones

- Entidades: sin @Data; @Getter/@Setter/@Builder/@AllArgsConstructor/@NoArgsConstructor(PROTECTED)/@EqualsAndHashCode(onlyExplicitlyIncluded)/@ToString(exclude)
- Enums: @Enumerated(EnumType.STRING)
- PK compuestas: @EmbeddedId con @Embeddable
- DTOs: record. Create validado; Update con nullValuePropertyMappingStrategy=IGNORE
- Mappers: MapStruct componentModel="spring", unmappedTargetPolicy=IGNORE

## 6. Modelo de datos

- 17 tablas, 8 enums, 7 FK compuestas
- CHECKs + índice parcial + índices sobre FK
- Informe consolidado por estudiante+unidad; snapshot congelado en detalle_informe_oa
- Datos derivados calculados; reglas críticas delegadas a capa Servicio

## 7. Stack tecnológico

| Herramienta | Versión | Uso |
|---|---|---|
| Java | 25 | Lenguaje |
| Spring Boot | 4.1.1 | Framework |
| Spring Data JPA/WebMVC/Validation | incluido | Persistencia/API/Validación |
| PostgreSQL | 17 | BD |
| Flyway | gestionado por SB | Migraciones |
| MapStruct | 1.6.3 | Mapping |
| Lombok | gestionado por SB | Boilerplate |
| springdoc-openapi | 3.1.0 | Swagger UI |
| Maven Wrapper | incluido | Build |
| Docker Compose | v2 | PostgreSQL local |

## 8. Requisitos previos

- JDK 25
- Docker Desktop (WSL2) o PostgreSQL 17
- Git

## 9. Instalación y configuración

### 9.1. Clonar
```bash
git clone <url-repositorio>
cd Backend-MPAZ_PRO_6-SpringBoot
```

### 9.2. Variables de entorno (.env)

El proyecto usa un unico archivo `.env` en la raiz (ver `.env.example`):

```bash
copy .env.example .env   # Windows
cp .env.example .env     # Linux/macOS
```

Solo contiene lo que varia por entorno (sin duplicados ni valores fijos):

| Variable | Default | Descripcion |
|---|---|---|
| `POSTGRES_DB` | `mpaz` | Nombre de la BD |
| `POSTGRES_USER` | `mpaz` | Usuario de la BD |
| `POSTGRES_PASSWORD` | `secret` | Contrasena (cambiar en entornos reales) |
| `POSTGRES_PORT` | `5432` | Opcional, solo si el 5432 esta ocupado |
| `DB_HOST` | `localhost` | Opcional, solo si la BD no esta en local |

No se versiona: `.env` esta en `.gitignore`. Solo `.env.example` se commitea.

### 9.3. Como lee cada parte el mismo `.env` (el "hueco")

- **Docker Compose** (`compose.yaml`) lee el `.env` automaticamente si esta junto al archivo. Sintaxis `${VAR:-default}`.
- **Spring Boot** (`application.yaml`) lee variables de entorno del proceso con sintaxis `${VAR:default}`, NO carga el `.env` solo.

Por eso `application.yaml` incluye:

```yaml
spring:
  config:
    import: optional:file:.env[.properties]
```

Sin esa linea, al correr con `./mvnw spring-boot:run` la app ignoraria tu `.env` y usaria los defaults, aunque `docker compose up` si los haya usado. Con esa linea, ambos leen el mismo archivo.

Si aun asi la app no toma un cambio del `.env`: reinicia el proceso (las variables se leen al arrancar, no en caliente) o exportalas a mano en tu IDE.

### 9.4. Levantar BD con Docker
```bash
docker compose up -d
docker compose ps
```

### 9.5. application.yaml
`datasource` usa `${POSTGRES_DB/USER/PASSWORD/PORT:default}` con los mismos nombres del `.env`. `ddl-auto: validate` y Flyway habilitado se mantienen fijos (no van al `.env`).

## 10. Ejecución

```cmd
.\mvnw.cmd clean spring-boot:run
```

Swagger: http://localhost:8080/swagger-ui.html

## 11. Estructura del proyecto

Ver estructura feature-based arriba. DTOs en dto/request y dto/response. Mappers/interfaces MapStruct.

## 12. Migraciones Flyway

- V1__esquema.sql: tablas, CHECKs, 7 FK compuestas, índice parcial, índices
- V2__seed.sql: datos ficticios coherentes con spec

Política: no editar migraciones aplicadas; crear V3__ si corresponde. ddl-auto=validate.

## 13. Convenciones de desarrollo

- DTOs record con validación
- Entidades sin @Data
- Mappers componentModel="spring"
- Relaciones vía getReferenceById en Service
- PK compuestas con @EmbeddedId

## 14. Datos de prueba (Seed)

| Usuario | Rol | Username | Password |
|---|---|---|---|
| Patricia Anilín | UTP | utp | utp12345 |
| Diego Salinas | DOCENTE | docente | doc12345 |
| Camila Rivas | ESTUDIANTE | camila | alu12345 |
| Mateo Cortés | ESTUDIANTE | mateo | alu12345 |
| Javiera Muñoz | ESTUDIANTE | javiera | alu12345 |
| Benjamín Ortiz | ESTUDIANTE | benjamin | alu12345 |

## 15. Pruebas

Compila y arranca con validate+Flyway. Se recomienda tests para reglas sección 8, informe vs snapshot, integridad.

## 16. Flujo de trabajo con Git

Commits atómicos, mensajes claros. No editar migraciones ya aplicadas.

## 17. Solución de problemas comunes

| Problema | Solución |
|---|---|
| Puerto 5432 ocupado | Cambiar mapeo en compose.yaml |
| Flyway/validate difiere | Revisar entidades vs V1; dropear BD o crear V3 |
| MapStruct no genera | Verificar annotation processors; mvn compile |
| FK compuesta falla | Asegurar unidad_id presente al persistir |

## 18. Referencias

- especificaciones/mpaz_pro_modelo_definitivo.md (autoritativo)
- especificaciones/mpaz_pro_v4_lean.md (intermedio)
- especificaciones/1-modelado.md (inicial)
- Spring Boot, Spring Data JPA, Flyway, MapStruct, PostgreSQL, springdoc-openapi

## 19. Créditos y contribución

Fines académicos. Legibilidad, trazabilidad y coherencia entre modelo/código/migraciones.

## 20. Licencia

Uso académico.