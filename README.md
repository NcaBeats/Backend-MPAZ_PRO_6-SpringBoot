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
- [21. Autenticación y Seguridad](#21-autenticación-y-seguridad)
- [22. Rutas expuestas](#22-rutas-expuestas)

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

- Rol UTP: autoriza/revisa unidades según estado y administra el registro de usuarios.
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
- Flyway como fuente de verdad; ddl-auto: none (SQLite no valida tipos de forma fiable)
- Datos de demo en Java (`DataInitializer`), no en una migración SQL

### 5.2. Estructura de paquetes

```
src/main/java/com/example/mpaz_pro_6springboot/
├── common/enums/
├── common/config/        (SqliteDirectoryInitializer, PasswordEncoderConfig)
├── common/security/      (CustomUserDetails, CustomUserDetailsService, SecurityConfig)
├── common/seed/          (DataInitializer: datos de demo)
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
| Spring Security | gestionado por SB | Autenticación y autorización con HTTP Basic (stateless) |
| Spring Data JPA/WebMVC/Validation | incluido | Persistencia/API/Validación |
| SQLite | 3 (sqlite-jdbc) | BD embebida en un archivo local |
| Flyway | gestionado por SB | Migraciones |
| MapStruct | 1.6.3 | Mapping |
| Lombok | gestionado por SB | Boilerplate |
| springdoc-openapi | 3.1.0 | Swagger UI |
| Maven Wrapper | incluido | Build |

## 8. Requisitos previos

- JDK 25
- Git

No se necesita Docker ni un servidor de base de datos: SQLite vive en `data/mpaz.db`.

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
| `SQLITE_PATH` | `./data/mpaz.db` | Ruta del archivo SQLite (relativa al directorio de ejecucion o absoluta). El directorio se crea solo |
| `SERVER_PORT` | `8080` | Opcional, puerto de la app (ver `server.port` en `application.yaml`) |

No se versiona: `.env` esta en `.gitignore`. Solo `.env.example` se commitea.

### 9.3. Como lee Spring el `.env`

- **Spring Boot** (`application.yaml`) lee variables de entorno del proceso con sintaxis `${VAR:default}`, NO carga el `.env` solo.

Por eso `application.yaml` incluye:

```yaml
spring:
  config:
    import: optional:file:.env[.properties]
```

Sin esa linea, al correr con `.\mvnw.cmd spring-boot:run` la app ignoraria tu `.env` y usaria los defaults. Con esa linea, la app lee el mismo archivo.

Si aun asi la app no toma un cambio del `.env`: reinicia el proceso (las variables se leen al arrancar, no en caliente) o exportalas a mano en tu IDE.

### 9.4. Base de datos SQLite

No hay que levantar nada: en el primer arranque Flyway crea `data/mpaz.db` con las 17 tablas y `DataInitializer` carga los datos de demo.

- Reiniciar los datos: detener la app, borrar `data/mpaz.db` (y sus `-journal`/`-wal`/`-shm`) y arrancar de nuevo.
- Desactivar el seed: `mpaz.seed.enabled: false` en `application.yaml` (arranca con la base vacia).
- El directorio `data/` esta en `.gitignore`: no se versiona.

### 9.5. application.yaml

`datasource` usa `jdbc:sqlite:${SQLITE_PATH:./data/mpaz.db}` con `foreign_keys=on` (SQLite ignora las FK por conexion si no se activan) y `busy_timeout=5000`. `ddl-auto: none` y Flyway habilitado se mantienen fijos (no van al `.env`). El pool de Hikari se limita a 1 conexion porque SQLite admite un solo escritor.

## 10. Ejecución

```cmd
.\mvnw.cmd clean spring-boot:run
```

En el primer arranque se crea `data/mpaz.db` (Flyway aplica `V1__esquema.sql`) y `DataInitializer` carga los datos de demo. No hay pasos manuales ni contenedores.

- Swagger: http://localhost:8080/swagger-ui.html
- Autenticación: HTTP Basic con los usuarios del seed (ver sección 14). `POST /api/auth/login` valida las credenciales y devuelve el usuario; `GET /api/auth/me` requiere autenticación. El registro (`POST /api/auth/register`) está reservado a la UTP.
- Un segundo arranque no duplica datos: el inicializador detecta que ya hay usuarios y no hace nada.
- Para volver al estado inicial: detener la app, borrar `data/mpaz.db` y arrancar de nuevo.
- Datos listos para consultar: 4 unidades, 12 actividades (3 por unidad), 4 intentos y 1 informe pedagogico de Camila (OA 1: 2/3, OA 2: 1/3).

## 11. Estructura del proyecto

Ver estructura feature-based arriba. DTOs en dto/request y dto/response. Mappers/interfaces MapStruct.

## 12. Migraciones Flyway

- `V1__esquema.sql`: 17 tablas, CHECKs, 7 FK compuestas (inline: SQLite no permite `ALTER TABLE ... ADD CONSTRAINT`), índice parcial e índices, todo en dialecto SQLite.
- No hay migración de seed: los datos de demo los carga `common/seed/DataInitializer.java` al arrancar.

Política: no editar migraciones aplicadas; crear `V2__...` si corresponde. `ddl-auto: none`.

Nota SQLite: los `id` son `INTEGER PRIMARY KEY` (autogenerados 1..N), `boolean` se guarda como 0/1 y `timestamp` como epoch en milisegundos (comportamiento por defecto de sqlite-jdbc). Ver el anexo "Motor SQLite" de la especificación.

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

Las contrasenas se guardan con BCrypt real generado en el arranque (no hay hashes fijos en el repositorio). El seed es idempotente: si ya existen usuarios, no se ejecuta.

El usuario `utp` es el administrador inicial de la demo; el rol UTP es el unico que puede registrar usuarios. En produccion hay que cambiar su contrasena, desactivar el seed (`mpaz.seed.enabled: false`) y provisionar el primer UTP por un mecanismo controlado, porque el registro no es publico.

Contenido cargado: 1 curso (6A, 2026), 6 usuarios, 4 asignaturas, 4 unidades, 8 OA, 8 contenidos, 8 recursos, 12 actividades, 6 preguntas, 19 alternativas, 4 asignaciones (con prueba final habilitada), 8 `asignacion_oa`, 12 `asignacion_actividad`, 4 intentos (Camila con 2, el segundo completado; Mateo 1; Javiera en progreso; Benjamín sin intentos), 12 respuestas y 1 informe de Camila con 2 detalles (OA 1: 2/3, OA 2: 1/3).

El snapshot del informe coincide con el recalculo del ultimo intento completado, y `es_correcta` coincide con la alternativa elegida.

## 15. Pruebas

`.\mvnw.cmd test` (22 tests) usa SQLite en memoria: no requiere servicios externos.

- `contextLoads`: el contexto arranca con Flyway + JPA.
- `AuthControllerTest`: login y `/me` con Basic, registro solo para UTP, validación de estudiante con curso, duplicados y rutas protegidas (401/403).
- `DataInitializerTest`: conteos del seed, contraseñas verificables, `PRAGMA foreign_keys`, round-trip de `Instant`, índice parcial de la prueba final, FK compuestas y coherencia del snapshot del informe con el recálculo.
- `SqliteDirectoryInitializerTest`: creación del directorio del archivo SQLite y omisión para bases en memoria.

## 16. Flujo de trabajo con Git

Commits atómicos, mensajes claros. No editar migraciones ya aplicadas.

## 17. Solución de problemas comunes

| Problema | Solución |
|---|---|
| Puerto 8080 ocupado | Buscar el PID con `netstat -ano` (linea `:8080`) y cerrarlo con `taskkill /PID <pid> /F` |
| Quiero reiniciar los datos | Detener la app y borrar `data/mpaz.db` (y `-journal`/`-wal`/`-shm`) |
| `database is locked` | Dejar el pool en 1 conexion y no abrir el archivo con otro programa mientras corre la app |
| Las FK no se validan | Verificar `foreign_keys=on` en la URL JDBC (es por conexion) |
| Flyway falla al migrar | Revisar `V1__esquema.sql`; si el archivo quedo a medias, borrar `data/mpaz.db` |
| MapStruct no genera | Verificar annotation processors; mvn compile |
| FK compuesta falla | Asegurar unidad_id presente al persistir y que coincida con la de la entidad referenciada |

## 18. Referencias

- especificaciones/mpaz_pro_modelo_definitivo.md (autoritativo, incluye anexo "Motor SQLite")
- especificaciones/mpaz_pro_v4_lean.md (intermedio)
- especificaciones/1-modelado.md (inicial)
- Spring Boot, Spring Data JPA, Flyway, MapStruct, SQLite (sqlite-jdbc), springdoc-openapi

## 19. Créditos y contribución

Fines académicos. Legibilidad, trazabilidad y coherencia entre modelo/código/migraciones.

## 20. Licencia

Uso académico.

## 21. Autenticación y Seguridad

La aplicación usa **HTTP Basic Auth stateless** con **BCrypt** para las contraseñas. No hay JWT ni sesiones HTTP: el cliente envía `Authorization: Basic ...` en cada solicitud.

### 21.1. Configuración
- **Dependencia**: `spring-boot-starter-security` en `pom.xml`.
- **PasswordEncoder**: `BCryptPasswordEncoder` (configurado en `common/config/PasswordEncoderConfig`).
- **UserDetailsService**: `common/security/CustomUserDetailsService` carga usuarios desde `UsuarioRepository` por `username`.
- **Autenticación**: Spring Security configura el `DaoAuthenticationProvider` global a partir de `CustomUserDetailsService` y `PasswordEncoder`.
- **Sesiones**: `SessionCreationPolicy.STATELESS`; no se crea `JSESSIONID`.
- **CSRF**: deshabilitado porque la API no usa cookies de sesión.
- **Autorización por métodos**: `@EnableMethodSecurity` habilitado para futuras reglas con `@PreAuthorize`.
- **Rutas**: ver sección 22.

### 21.2. Roles y autoridades
Los roles se mapean directamente desde el enum `Rol`:

| Rol | Autoridad asignada | Uso previsto |
|---|---|---|
| `UTP` | `ROLE_UTP` | Administración del sistema: registro de usuarios, gestión y autorización de unidades, supervisión general. |
| `DOCENTE` | `ROLE_DOCENTE` | Gestión de asignaciones, validación de informes pedagógicos. |
| `ESTUDIANTE` | `ROLE_ESTUDIANTE` | Acceso a contenido/asignaciones propias, resolución de actividades. |

La conversión se realiza en `CustomUserDetails` con `new SimpleGrantedAuthority("ROLE_" + rol.name())`, lo que permite usar `hasRole("UTP")`, `hasAnyRole(...)`, etc.

### 21.3. Login, registro y usuario actual

`POST /api/auth/login` no recibe body: las credenciales viajan en el header Basic. Devuelve `200 OK` con el usuario autenticado; sin credenciales o con credenciales inválidas devuelve `401 Unauthorized`.

```bash
curl -i -u camila:alu12345 -X POST http://localhost:8080/api/auth/login
```

`GET /api/auth/me` devuelve el usuario autenticado en la solicitud actual (también con Basic).

```bash
curl -i -u camila:alu12345 http://localhost:8080/api/auth/me
```

El registro es administrativo: solo un usuario con `ROLE_UTP` puede llamar a `POST /api/auth/register`. El cuerpo usa el mismo contrato que `POST /api/usuarios`: `nombre`, `username`, `password`, `rol` y `cursoId`. `cursoId` es obligatorio cuando `rol` es `ESTUDIANTE` y debe omitirse para `UTP`/`DOCENTE`.

```bash
curl -i -u utp:utp12345 -X POST \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Nuevo Alumno","username":"nuevo.alumno","password":"secreto123","rol":"ESTUDIANTE","cursoId":1}' \
  http://localhost:8080/api/auth/register
```

Respuesta `201 Created` con `UsuarioResponse` y `Location: /api/usuarios/{id}`. Errores: `400` por validación o estudiante sin curso, `401` sin credenciales, `403` si el usuario autenticado no es UTP, `404` si el curso no existe y `409` si el `username` ya está registrado.

No hay endpoint de logout: con Basic el cliente simplemente deja de enviar las credenciales. Las credenciales deben viajar siempre por HTTPS en un entorno real.

### 21.4. Probar rutas protegidas
Con la app corriendo (`.\mvnw.cmd spring-boot:run`):

```bash
# Sin credenciales -> 401
curl -i http://localhost:8080/api/usuarios

# Estudiante -> 403; UTP -> 200
curl -i -u camila:alu12345 http://localhost:8080/api/usuarios
curl -i -u utp:utp12345 http://localhost:8080/api/usuarios
```

Las credenciales válidas son las del **DataInitializer** (ver sección 14).

## 22. Rutas expuestas

### 22.1. Seguridad por ruta
| Ruta | Acceso | Motivo |
|---|---|---|
| `/actuator/health`, `/actuator/info` | Público | Health/info de la aplicación |
| `/swagger-ui.html`, `/swagger-ui/**`, `/v3/api-docs/**`, `/v3/api-docs.yaml` | Público | Documentación OpenAPI/Swagger |
| `POST /api/auth/login` | Público (requiere Basic para autenticar) | Valida credenciales y devuelve el usuario |
| `GET /api/auth/me` | Autenticado | Usuario autenticado en la solicitud |
| `POST /api/auth/register` | `ROLE_UTP` | Registro administrativo de usuarios |
| `/api/usuarios/**` | `ROLE_UTP` | Gestión de usuarios |
| **Todas las demás rutas** (`/**`) | **Público por ahora** | Pendiente de aplicar reglas por rol y por dueño |

### 22.2. Configuración actual
```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/actuator/health", "/actuator/info").permitAll()
    .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/v3/api-docs.yaml").permitAll()
    .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
    .requestMatchers(HttpMethod.POST, "/api/auth/register").hasRole("UTP")
    .requestMatchers("/api/auth/me").authenticated()
    .requestMatchers("/api/usuarios/**").hasRole("UTP")
    .anyRequest().permitAll()
)
```

A medida que se añadan controladores con prefijos por rol, se actualizarán estas reglas. Las rutas de datos siguen públicas para no romper la demo; aplicar autorización por rol y por dueño es el siguiente paso de seguridad (sección 8 de la especificación).
