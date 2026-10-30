# AGENTS.md — MPAZ PRO 6° (Spring Boot + PostgreSQL)

Guía operativa para futuras sesiones. Léela antes de tocar código, migraciones o Docker.

## 1. Estado actual

- Proyecto en refactor completo sobre un modelo de datos nuevo (26 tablas).
- Rama de trabajo: `feature/refactor`.
- Solo existe el bootstrap de Spring Boot y una configuración temporal de seguridad:
  - `src/main/java/com/example/mpaz_pro_6springboot/MpazPro6SpringBootApplication.java`
  - `src/main/java/com/example/mpaz_pro_6springboot/common/security/SecurityConfig.java` (`permitAll` temporal)
- **Ya existen las 26 entidades JPA** de la capa `model`, organizadas por feature:
  - `identidad/model`: `Usuario`, `Rol`, `Curso`, `Nivel`, `Seccion`.
  - `curriculo/model`: `Asignatura`, `Unidad`, `ObjetivoAprendizaje`,
    `ObjetivoAprendizajeUnidad`, `Contenido`, `ObjetivoAprendizajeContenido`,
    `Recurso`, `Actividad`, `Pregunta`, `Alternativa`, `AlternativaPregunta`,
    `PreguntaActividad`, `EstadoAutorizacion`, `TipoActividad`, `TipoRecurso`.
  - `docencia/model`: `AsignaturaCurso`, `UsuarioCurso`.
  - `evaluacion/model`: `Intento`, `Respuesta`.
  - `informe/model`: `InformePedagogico`, `DetalleInformePedagogico`.
- Las entidades usan solo el lado propietario de las relaciones (`@ManyToOne`),
  sin colecciones bidireccionales, para evitar ciclos y cargas accidentales.
- **Todavía no hay** repositorios, servicios, controladores, DTOs, mappers ni datos seed.
- El esquema se aplica con Flyway y ya fue validado contra PostgreSQL 17:
  - 26 tablas
  - 34 claves foráneas
  - 18 restricciones `UNIQUE`
- Reglas de negocio ya reflejadas en el esquema:
  - Un OA puede pertenecer a varias unidades (currículum en espiral) mediante
    `objetivo_aprendizaje_unidad`.
  - Un contenido pertenece a una única unidad (`contenido.unidad_id`) y esa
    unidad es inmutable (trigger).
  - El informe pedagógico se emite en el contexto de una unidad
    (`informe_pedagogico.unidad_id`).
- El modelo anterior (SQLite, 17 tablas, enums, asignaciones) fue descartado. No lo reintroduzcas.

## 2. Fuentes de verdad

| Fuente | Uso |
|---|---|
| `mpaz_modelo_refactorizado_oracle.sql` | DDL de referencia del modelo (Oracle 21c) |
| `src/main/resources/db/migration/V1__init.sql` | Esquema inicial en PostgreSQL (no se edita) |
| `src/main/resources/db/migration/V2__oa_unidad_contenido_informe.sql` | OA multi-unidad, contenido por unidad e informe por unidad |
| `specs/1.relaciones_modelo_mpaz.md` | Relaciones, cardinalidades y restricciones |
| `README.md` | Puesta en marcha y variables de entorno |
| `compose.yaml` / `Dockerfile` | Infraestructura local |

Regla de autoridad: **lo que corre es el conjunto de migraciones Flyway**. Si el DDL
Oracle y las migraciones difieren, las migraciones mandan para el código, pero la
diferencia debe corregirse en ambos lados y documentarse en `specs/`.

## 3. Stack

- Java 25
- Spring Boot 4.1.1
- Spring Data JPA / WebMVC / Validation / Security
- PostgreSQL 17
- Flyway (con `flyway-database-postgresql`)
- MapStruct 1.6.3
- Lombok
- springdoc-openapi 3.1.0 (`/swagger-ui.html`)
- Maven Wrapper 3.9.16

## 4. Comandos (Windows / PowerShell)

### Docker

```powershell
copy .env.example .env
docker compose up -d --build          # app + postgres
docker compose up -d db               # solo postgres
docker compose ps
docker compose logs -f app
docker compose down                   # conserva el volumen
docker compose down -v                # borra la BD y reinicia desde cero
docker compose config                 # valida el compose
docker compose build app              # solo la imagen de la app
```

Si Docker no responde, abrir Docker Desktop (o iniciar el servicio `com.docker.service`).

### Desarrollo local

```powershell
docker compose up -d db
.\mvnw.cmd spring-boot:run
```

La app lee `.env` con `spring.config.import: optional:file:.env[.properties]`.
En local `DB_HOST=localhost`; dentro de compose se sobreescribe a `db`.

### Base de datos

```powershell
docker compose exec db psql -U mpaz -d mpaz
docker compose exec db psql -U mpaz -d mpaz -c "\dt"
docker compose exec db psql -U mpaz -d mpaz -c "SELECT version, description, success FROM flyway_schema_history ORDER BY installed_rank;"
```

### Build y tests

```powershell
.\mvnw.cmd clean package -DskipTests
.\mvnw.cmd test
```

No hay tests todavía. Si se agregan, usar **Testcontainers con PostgreSQL** (ver §8.6).

## 5. Variables de entorno

Solo se versiona `.env.example`; `.env` está en `.gitignore`.

| Variable | Default | Uso |
|---|---|---|
| `POSTGRES_DB` | `mpaz` | Nombre de la base |
| `POSTGRES_USER` | `mpaz` | Usuario |
| `POSTGRES_PASSWORD` | `mpaz` | Password |
| `POSTGRES_PORT` | `5432` | Puerto del host para Postgres |
| `DB_HOST` | `localhost` | Host de la BD (`db` en compose) |
| `SERVER_PORT` | `8080` | Puerto interno de Spring Boot |
| `APP_PORT` | `8080` | Puerto del host para la app en compose |

No agregues `SPRING_*` duplicados al `.env`; usa las variables de arriba.
Los cambios de `.env` requieren recrear contenedores:

```powershell
docker compose up -d --force-recreate
```

## 6. Base de datos y Flyway

### 6.1. Esquema

- Esquema `public`; 26 tablas + `flyway_schema_history`.
- IDs: `BIGINT GENERATED BY DEFAULT AS IDENTITY` (se permite insertar ids explícitos en seeds).
- Tipos: `BIGINT` (ids/FK), `INTEGER` (años, conteos, intentos), `TEXT`, `VARCHAR(n)`,
  `BOOLEAN`, `TIMESTAMPTZ`.
- Todas las columnas FK son `NOT NULL`.
- No hay reglas `ON DELETE`/`ON UPDATE`: PostgreSQL aplica `NO ACTION` (sin cascada).
- Los catálogos reemplazan a los enums: `rol`, `estado_autorizacion`, `tipo_actividad`,
  `tipo_recurso`, `nivel`, `seccion`. **No crees enums Java duplicando estos catálogos.**
- `respuesta` tiene FK compuesta `(pregunta_id, alternativa_id)` →
  `alternativa_pregunta (pregunta_id, alternativa_id)`.
- `actividad.contenido_id`, `pregunta.recurso_id` y `alternativa.recurso_id` son
  `NOT NULL`: al sembrar datos hay que crear contenidos/recursos primero.
- **OA ↔ unidad es N:M** vía `objetivo_aprendizaje_unidad`
  (`UNIQUE (objetivo_aprendizaje_id, unidad_id)`). `objetivo_aprendizaje` ya no
  tiene `unidad_id`. No vuelvas a asumir 1:N.
- **`contenido.unidad_id` es `NOT NULL` e inmutable.** Un trigger
  `trg_contenido_unidad_inmutable` rechaza cualquier `UPDATE OF unidad_id`
  (`SQLSTATE 23514`). Para mover un contenido hay que crear uno nuevo.
- **La unidad de una actividad se hereda del contenido**:
  `actividad.contenido_id → contenido.unidad_id`. No agregues `actividad.unidad_id`.
- **`objetivo_aprendizaje_contenido` guarda `unidad_id`** y usa FK compuestas:
  - `(contenido_id, unidad_id)` → `contenido (contenido_id, unidad_id)`
  - `(objetivo_aprendizaje_id, unidad_id)` →
    `objetivo_aprendizaje_unidad (objetivo_aprendizaje_id, unidad_id)`

  Por tanto, un contenido solo puede vincularse a un OA ofrecido en su misma unidad.
- **`informe_pedagogico.unidad_id` es `NOT NULL`**. El detalle sigue siendo por OA;
  la coherencia OA/unidad se valida en servicio.

### 6.2. Migraciones

- `V1__init.sql` y `V2__oa_unidad_contenido_informe.sql` ya están aplicadas: **no se editan**.
- Todo cambio va en `V3__descripcion.sql`, `V4__...`, etc. (dialecto PostgreSQL).
- Nombres: `V<n>__snake_case.sql` (por ejemplo `V3__seed_catalogos.sql`).
- V2 es incremental y contiene lógica de datos: crea la tabla puente OA-unidad,
  migra la asignación 1:N existente, agrega columnas `NOT NULL`, sustituye FK
  simples por compuestas y crea el trigger de inmutabilidad. Si se parte de una
  base con datos, revisar las validaciones `DO $$` de V2 antes de aplicarla.
- No usar `PRAGMA`, `.conf` de `executeInTransaction` ni sintaxis SQLite.
- `spring.jpa.hibernate.ddl-auto: validate`: el esquema es propiedad de Flyway.
- Para cambios destructivos o de datos, documentar el `up` y el impacto en `specs/`.

### 6.3. Reset

```powershell
docker compose down -v
docker compose up -d --build
```

`down -v` borra el volumen `postgres_data`; en el siguiente arranque Flyway aplica
`V1` y `V2` desde cero.

## 7. Docker

- `compose.yaml`:
  - `db`: `postgres:17-alpine`, volumen `postgres_data`, healthcheck `pg_isready`.
  - `app`: build local, espera `db` healthy, publica `${APP_PORT}:8080`.
- `Dockerfile`: multi-stage `maven:3.9-eclipse-temurin-25` → `eclipse-temurin:25-jre`,
  usuario no-root `spring`, `java -jar app.jar`.
- `.dockerignore` excluye `target/`, `.git/`, `.env`, docs y datos locales.
- La app no debe arrancar antes de que Postgres esté healthy (`depends_on`).
- Puertos configurables: `POSTGRES_PORT` (host) y `APP_PORT` (host).

## 8. Convenciones de código

Paquete base: `com.example.mpaz_pro_6springboot`.

### 8.1. Paquetes por feature

```
common/        (config, security, errores)
identidad/     (usuario, rol, curso, nivel, seccion)
curriculo/     (asignatura, unidad, OA, OA-unidad, contenido, recurso, actividad, pregunta, alternativa)
docencia/      (asignatura_curso, usuario_curso)
evaluacion/    (intento, respuesta)
informe/       (informe_pedagogico, detalle_informe_pedagogico)
```

Cada feature: `model/`, `repository/`, `dto/request/`, `dto/response/`, `mapper/`,
`service/`, `controller/`.

### 8.2. Entidades

- Sin `@Data`. Usar `@Getter/@Setter`, `@Builder`, `@AllArgsConstructor`,
  `@NoArgsConstructor(access = PROTECTED)`, `@EqualsAndHashCode(onlyExplicitlyIncluded = true)`,
  `@ToString(exclude = {...})` en relaciones lazy.
- ID: `@Id @GeneratedValue(strategy = GenerationType.IDENTITY)` con `Long`.
- **Este modelo no tiene PK compuestas**: no usar `@EmbeddedId`. Las tablas puente
  (`objetivo_aprendizaje_unidad`, `objetivo_aprendizaje_contenido`, etc.) tienen PK
  propia y una restricción `UNIQUE` sobre la pareja.
- Relaciones: `@ManyToOne(fetch = FetchType.LAZY)` + `@JoinColumn(name = "...")`.
  En servicios, resolver con `getReferenceById(...)`.
- OA ↔ unidad: modelar como `@ManyToMany` con `@JoinTable(name =
  "objetivo_aprendizaje_unidad", ...)`, no como `@ManyToOne` desde el OA.
- `Contenido.unidad`: `@ManyToOne(optional = false, fetch = LAZY)`. No exponer un
  setter de unidad para actualizaciones: la columna es inmutable en la BD.
- `ObjetivoAprendizajeContenido`: entidad con PK propia y claves `contenido` +
  `unidad`/`objetivo` + `unidad`. Al insertar, usar la unidad del contenido; la FK
  compuesta rechaza combinaciones incoherentes.
- `InformePedagogico.unidad`: `@ManyToOne(optional = false)`. Validar en servicio
  que cada OA del detalle esté ofrecido en esa unidad.
- Tipos Java: `Instant` para `timestamptz`, `boolean`/`Boolean` para `boolean`,
  `String` para `text`/`varchar`, `Integer` para `integer`, `Long` para `bigint`.
- Hibernate mapea camelCase a snake_case por defecto. Si un nombre no coincide,
  declarar `@Table(name = "...")` / `@Column(name = "...")` explícitamente.
- No crear `enum` para `rol`, `estado_autorizacion`, `tipo_actividad`, `tipo_recurso`,
  `nivel` o `seccion`: son tablas.

### 8.3. DTOs y mappers

- DTOs `record` con Bean Validation.
- Update DTOs: todos los campos opcionales; MapStruct
  `nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE`.
- MapStruct: `componentModel = "spring"`, `unmappedTargetPolicy = ReportingPolicy.IGNORE`.
- Orden de annotation processors en `pom.xml`: Lombok → `mapstruct-processor` →
  `lombok-mapstruct-binding`. Si faltan implementaciones, `.\mvnw.cmd compile`.

### 8.4. Servicios y controladores

- Escrituras con `@Transactional`; lecturas con `@Transactional(readOnly = true)`.
- Reglas de negocio en servicio, no en controladores ni mappers.
- Controladores bajo `/api/...`; aún no hay rutas implementadas.
- Errores: cuando se agregue manejo global, usar `@RestControllerAdvice` con
  `400` (validación), `404` (no existe), `409` (conflicto/FK).

### 8.5. Seguridad

- `SecurityConfig` actual deja todo abierto (`permitAll`) solo para bootstrap.
- Al implementar autenticación: reemplazar por reglas reales, usar
  `BCryptPasswordEncoder` para `usuario.password_hash` y no dejar rutas abiertas.
- `spring-boot-starter-security` y `spring-security-crypto` ya están en el `pom.xml`.

### 8.6. Tests

- No usar SQLite ni H2 para validar el esquema: el DDL es PostgreSQL.
- Recomendado: Testcontainers (`spring-boot-testcontainers`,
  `org.testcontainers:postgresql`, `org.testcontainers:junit-jupiter`) con
  `@ServiceConnection` y `postgres:17-alpine`.
- Los tests requieren Docker corriendo.
- `.\mvnw.cmd test` debe seguir funcionando sin servicios externos manuales.

## 9. Reglas de negocio y huecos conocidos

- **Unidad de la actividad:** se hereda de `contenido.unidad_id` (NOT NULL e
  inmutable). No hay `actividad.unidad_id`; no lo agregues sin una decisión de modelo.
- **Unidad del contenido:** no se puede cambiar (trigger). Para reubicar un contenido,
  crear uno nuevo en la unidad destino y migrar los datos asociados.
- **OA multi-unidad:** un OA puede estar en varias unidades. Al vincularlo a un
  contenido, usar la unidad del contenido; la FK compuesta exige que el OA esté
  ofrecido en esa unidad. No asumir que el OA tiene una única unidad.
- **Informe pedagógico:** `unidad_id` es obligatorio. Validar en servicio que cada OA
  del detalle pertenezca a esa unidad (el detalle no guarda `unidad_id`) y que
  `usuario_id` tenga rol de estudiante (el esquema no lo restringe).
- **`usuario_curso`:** no distingue rol ni tipo de participación; validar en servicio.
- **Alternativas correctas:** el esquema permite varias `correcta = true` por pregunta;
  si la regla es de selección única, validarlo en servicio.
- **Borrados:** sin cascada; si un padre tiene hijos, la FK falla. Traducir a `409`.
- **`actividad.max_intentos`:** nullable; definir la semántica (nulo = sin límite) en servicio.
- **Catálogos:** deben existir antes de crear usuarios, unidades, contenidos, etc.
- **`objetivo_aprendizaje.codigo`:** no tiene `UNIQUE`. Si los códigos deben ser
  únicos por asignatura, evaluar agregar `asignatura_id` y `UNIQUE (asignatura_id,
  codigo)` en una migración futura.
- **Seed:** aún no existe. Si se agrega, crear `V3__seed_*.sql` en orden de FK.

## 10. Flujo de trabajo

- Commits atómicos y mensajes claros (`feat(...)`, `fix(...)`, `docs(...)`, `chore(...)`).
- No editar migraciones ya aplicadas; crear la siguiente versión.
- Si cambia el modelo: actualizar `specs/` y el DDL de referencia, y agregar migración.
- Verificar siempre con `docker compose config` y `docker compose up -d --build`.
- No commitear `.env`, `target/` ni volúmenes/datos locales.
- Archivos UTF-8 **sin BOM**. En PowerShell 5.1 evitar `Set-Content -Encoding utf8`
  para código; usar `[System.IO.File]::WriteAllText($path, $text, (New-Object System.Text.UTF8Encoding($false)))`.
- En PowerShell no usar `&&`, `head`, `tail` ni `grep`: usar `;`, `Select-Object`,
  `Select-String`.

## 11. Roadmap sugerido

1. Repositorios de las 26 tablas (las entidades JPA ya están creadas).
2. `V3__seed_*.sql` con catálogos y datos de demo (ids explícitos permitidos).
3. Autenticación y autorización por rol (login, BCrypt, JWT/sesión).
4. Servicios y controladores del dominio (currículo, docencia, evaluación, informes).
5. Reglas de negocio de §9 y manejo global de errores.
6. Tests de integración con Testcontainers.
