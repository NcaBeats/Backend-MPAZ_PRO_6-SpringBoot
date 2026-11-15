# MPAZ PRO 6° - Backend (Spring Boot + PostgreSQL)

Backend del sistema de gestión pedagógica MPAZ PRO 6°. Esta versión parte de un
modelo de datos nuevo (`mpaz_modelo_refactorizado_oracle.sql`) traducido a
PostgreSQL y ejecutado con Flyway sobre una base levantada con Docker.

## Stack

| Herramienta | Versión / uso |
|---|---|
| Java | 25 |
| Spring Boot | 4.1.1 |
| Spring Data JPA / WebMVC / Validation | incluido |
| Spring Security | incluido |
| PostgreSQL | 17 (contenedor) |
| Flyway | migraciones (`src/main/resources/db/migration`) |
| MapStruct | 1.6.3 |
| Lombok | gestionado por Spring Boot |
| springdoc-openapi | 3.1.0 |
| Maven Wrapper | incluido |

## Requisitos

- Docker Desktop (con Docker Compose v2)
- Opcional para desarrollo local: JDK 25 y el wrapper de Maven incluido

## Inicio rápido (todo en Docker)

```powershell
copy .env.example .env
docker compose up -d --build
```

- App: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- Health: http://localhost:8080/actuator/health
- PostgreSQL: `localhost:5432`, base/usuario/password `mpaz` (valores por defecto de `.env.example`)

En el primer arranque Flyway aplica `V1__init.sql`,
`V2__oa_unidad_contenido_informe.sql`,
`V3__actividad_curso_y_nivel_asignatura.sql`,
`V4__asignatura_curso_docente.sql`,
`V5__informe_pedagogico_fecha_corte.sql`,
`V6__seed_demo.sql` y
`V7__seed_contenido_largo.sql`, dejando 27 tablas, los datos de demostración y
los contenidos completos. El contenedor
`app` espera a que Postgres esté saludable
(`depends_on: condition: service_healthy`).

## Desarrollo local (solo Postgres en Docker)

```powershell
copy .env.example .env
docker compose up -d db
.\mvnw.cmd spring-boot:run
```

La app lee `.env` mediante `spring.config.import: optional:file:.env[.properties]`.
`DB_HOST` debe ser `localhost` en este modo (en compose se sobreescribe a `db`).

## Autenticación JWT

El backend es stateless: `POST /api/auth/login` devuelve un Bearer token que debe
enviarse en `Authorization: Bearer <token>`. El seed de demo (`V6__seed_demo.sql`)
crea estos usuarios con la contraseña `mpaz123`:

| Usuario | Rol |
|---|---|
| `utp` | UTP |
| `docente`, `docente2`, `docente3`, `docente4` | DOCENTE |
| `alumno`, `estudiante2`, `estudiante3`, `estudiante4` | ESTUDIANTE |

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"utp","password":"mpaz123"}'
```

## Conexión de la app Android

La guía paso a paso para terminal, Android Studio, IntelliJ, USB, Wi-Fi y
emulador está en el `README.md` del repositorio mobile
(`MPAZ_PRO_6/README.md`).

Resumen de URLs:

| Entorno | URL de la app | Requisito |
|---|---|---|
| USB | `http://127.0.0.1:8080/` | `adb reverse tcp:8080 tcp:8080` |
| Emulador | `http://10.0.2.2:8080/` | Backend en el mismo PC |
| Wi-Fi | `http://IP_DEL_PC:8080/` | Mismo Wi-Fi y puerto 8080 abierto |

En el teléfono, `127.0.0.1` es el propio teléfono. Para USB se crea el túnel
`adb reverse`; para Wi-Fi se compila con la IP LAN del PC. Si el APK se compila
con `127.0.0.1`, no conectará por Wi-Fi hasta recompilarlo.

En Android 17, una conexión Wi-Fi directa a la IP LAN requiere el permiso
runtime `ACCESS_LOCAL_NETWORK`. La app lo solicita al abrirse cuando detecta una
IP local; USB con `adb reverse` no lo necesita.

## Autorización de material pedagógico

Las escrituras de material (`unidades`, `contenidos`, `actividades` y `recursos`)
se validan en el servicio con `MaterialAccessPolicy`:

| Rol | Alcance de escritura |
|---|---|
| `UTP` | Todas las asignaturas |
| `DOCENTE` | Solo asignaturas con una fila en `asignatura_curso` para su usuario |
| `ESTUDIANTE` | Sin escritura de material |

Si un docente intenta crear, actualizar o eliminar material de una asignatura
que no tiene a cargo, la API responde `403 Forbidden`. La app móvil aplica el
mismo filtro al mostrar unidades y actividades en “Crear contenido”.

Un usuario UTP no puede desactivar su propia cuenta: la API responde `400` y la
app deshabilita el switch de esa fila.

## Informes pedagógicos

- `GET /api/informes-pedagogicos/preliminar?usuarioId=&unidadId=&fechaCorte=`
  calcula el consolidado por OA usando el último intento cerrado de cada
  actividad.
- `POST /api/informes-pedagogicos/desde-resultados` persiste el informe y sus
  detalles como snapshot (`fecha_corte` y `origen`).
- `DELETE /api/informes-pedagogicos/{id}` elimina primero
  `detalle_informe_pedagogico` y luego la cabecera. No existe cascada en el
  esquema; el servicio hace el borrado ordenado y responde `204`. Después del
  borrado, `GET /api/informes-pedagogicos/{id}` responde `404`.

## Variables de entorno

| Variable | Default | Uso |
|---|---|---|
| `POSTGRES_DB` | `mpaz` | Nombre de la base |
| `POSTGRES_USER` | `mpaz` | Usuario de la base |
| `POSTGRES_PASSWORD` | `mpaz` | Password de la base |
| `POSTGRES_PORT` | `5432` | Puerto del host para Postgres |
| `DB_HOST` | `localhost` | Host de la BD (`db` dentro de compose) |
| `SERVER_PORT` | `8080` | Puerto interno de Spring Boot |
| `APP_PORT` | `8080` | Puerto del host para la app en compose |
| `MPAZ_JWT_SECRET` | secreto de demo | Firma HS256 de los JWT |

`.env` está en `.gitignore`; solo se versiona `.env.example`.

## Migraciones Flyway

- `src/main/resources/db/migration/V1__init.sql`: esquema inicial en PostgreSQL
  (25 tablas, claves primarias identity, claves foráneas simples y compuestas).
- `src/main/resources/db/migration/V2__oa_unidad_contenido_informe.sql`: modelo
  aprobado de OA multi-unidad, contenido por unidad e informe por unidad
  (26 tablas en total, 34 FK, 18 `UNIQUE`).
- `src/main/resources/db/migration/V3__actividad_curso_y_nivel_asignatura.sql`:
  nivel obligatorio en asignatura y actividad publicada por curso
  (27 tablas en total, 37 FK, 19 `UNIQUE`).
- `src/main/resources/db/migration/V4__asignatura_curso_docente.sql`:
  asignación opcional del docente responsable en `asignatura_curso`
  (27 tablas en total, 38 FK, 19 `UNIQUE`).
- `src/main/resources/db/migration/V5__informe_pedagogico_fecha_corte.sql`:
  agrega `fecha_corte` y `origen` al informe pedagógico. Los informes existentes
  heredan su `fecha` como corte.
- `src/main/resources/db/migration/V6__seed_demo.sql`: roles, usuarios de demo
  (`mpaz123`), catálogos, cursos, unidades, contenidos, actividades, preguntas,
  alternativas, un intento y un informe de ejemplo.
- `src/main/resources/db/migration/V7__seed_contenido_largo.sql`: reemplaza las
  descripciones resumidas por los Markdown completos del seed mobile.
- Política: una migración aplicada no se edita; los cambios nuevos van en
  `V8__...`, `V9__...`, etc.
- `spring.jpa.hibernate.ddl-auto: validate`: el esquema es propiedad de Flyway.

### Reglas de modelo y datos aplicadas desde V2

- **Un OA puede pertenecer a varias unidades** (currículum en espiral de Chile):
  relación N:M mediante `objetivo_aprendizaje_unidad`.
- **Un contenido pertenece a una única unidad** (`contenido.unidad_id NOT NULL`),
  y esa unidad es **inmutable**: un trigger rechaza cualquier cambio. Las
  actividades heredan la unidad del contenido, por lo que no se duplica en
  `actividad`.
- **`objetivo_aprendizaje_contenido` guarda `unidad_id`** y usa FK compuestas para
  exigir que el contenido y el OA pertenezcan a la misma unidad.
- **El informe pedagógico se emite por unidad** (`informe_pedagogico.unidad_id
  NOT NULL`). El detalle sigue siendo por OA; la coherencia OA/unidad se valida
  en la capa de servicio.
- **La asignatura pertenece a un nivel** (`asignatura.nivel_id NOT NULL`), y su
  nombre es único dentro del nivel: `UNIQUE (nivel_id, nombre)`. La disciplina se
  repite por nivel como asignaturas distintas.
- **La actividad es una plantilla**: la configuración por curso (ventana,
  `habilitada`, `visible`, `max_intentos`) vive en `actividad_curso`, con
  `UNIQUE (actividad_id, curso_id)`. `max_intentos` nulo significa sin límite.
- **Reutilizar una actividad en otro contenido**: se actualiza
  `actividad.contenido_id` con `PUT /api/actividades/{id}`; la unidad se hereda
  del contenido destino. La app usa este flujo al elegir “Asociar Creada”.
- **El intento se registra contra `actividad_curso`** (`intento.actividad_curso_id
  NOT NULL`), no contra la actividad. Así el límite y los resultados quedan
  acotados al curso.
- **El docente a cargo se asigna por asignatura y curso** mediante
  `asignatura_curso.docente_id`, opcional y con FK a `usuario`. Si se informa,
  el servicio exige que su rol sea `DOCENTE`.

### Traducción Oracle → PostgreSQL

| Oracle | PostgreSQL |
|---|---|
| `NUMBER GENERATED ALWAYS AS IDENTITY` | `BIGINT GENERATED BY DEFAULT AS IDENTITY` |
| `NUMBER` (ids / FK) | `BIGINT` |
| `NUMBER` (conteos, años, intentos) | `INTEGER` |
| `VARCHAR2(n)` | `VARCHAR(n)` |
| `CLOB` | `TEXT` |
| `CHAR(1)` S/N | `BOOLEAN` |
| `TIMESTAMP` | `TIMESTAMPTZ` |

Se usa `GENERATED BY DEFAULT` para permitir seeds con ids explícitos sin
`OVERRIDING SYSTEM VALUE`.

## Reiniciar la base

```powershell
docker compose down -v
docker compose up -d --build
```

`down -v` elimina el volumen `postgres_data`, por lo que Flyway vuelve a aplicar
`V1__init.sql` hasta `V7__seed_contenido_largo.sql` desde cero, incluyendo los
datos de demostración y los contenidos largos.

## Pruebas

```powershell
.\mvnw.cmd test
```

En Linux/macOS:

```bash
bash ./mvnw test
```

La suite usa PostgreSQL embebido (Zonky), no requiere Docker y valida Flyway,
seed, JWT y el contrato mobile. Actualmente son **9 tests**: 5 de contrato, 3 de
JWT y 1 de contraseñas del seed.

## Estructura

```
src/main/java/com/example/mpaz_pro_6springboot/
├── MpazPro6SpringBootApplication.java
├── common/security/       # SecurityConfig, JWT, MaterialAccessPolicy, ReportAccessPolicy
├── identidad/model/       # Usuario, Rol, Curso, Nivel, Seccion
├── curriculo/model/       # Asignatura, Unidad, OA, Contenido, Actividad, Pregunta, ...
├── docencia/model/        # AsignaturaCurso, UsuarioCurso, ActividadCurso
├── evaluacion/model/      # Intento, Respuesta
└── informe/model/         # InformePedagogico, DetalleInformePedagogico
src/main/resources/
├── application.yaml
└── db/migration/
    ├── V1__init.sql
    ├── V2__oa_unidad_contenido_informe.sql
    ├── V3__actividad_curso_y_nivel_asignatura.sql
    ├── V4__asignatura_curso_docente.sql
    ├── V5__informe_pedagogico_fecha_corte.sql
    ├── V6__seed_demo.sql
    └── V7__seed_contenido_largo.sql
compose.yaml
Dockerfile
```

La capa `model` contiene las 27 entidades JPA con sus relaciones. El modelo
anterior basado en SQLite fue retirado; sobre estas entidades ya están
construidas las capas de repositorio, DTO, mapper, servicio y controlador.

## Solución de problemas

| Problema | Solución |
|---|---|
| Puerto 5432 u 8080 ocupado | Cambiar `POSTGRES_PORT` / `APP_PORT` en `.env` |
| La app no conecta a la BD | Verificar que `db` esté healthy: `docker compose ps` |
| Flyway falla | Revisar logs con `docker compose logs app`; si la base quedó a medias, `docker compose down -v` |
| Cambios de `.env` no se aplican | Reiniciar los contenedores: `docker compose up -d --force-recreate` |
| `permission denied` al usar Docker | Agregar el usuario al grupo `docker` o ejecutar el comando con `sudo` |
| Docente recibe `403` al crear material | Verificar que su usuario esté asignado a la asignatura en `asignatura_curso` |
