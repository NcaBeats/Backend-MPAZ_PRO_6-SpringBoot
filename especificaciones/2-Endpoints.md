# Endpoints implementados

Este documento resume las API REST implementadas hasta el momento para Asignatura, RecursoContenido, Curso, Unidad, ObjetivoAprendizaje, Contenido, Actividad, Pregunta, Intento, RespuestaEstudiante, la asociación de objetivos de aprendizaje a asignaciones, la asociación de actividades a asignaciones y las alternativas.

## Convenciones

- Base URL local: `http://localhost:8080`.
- Los cuerpos de solicitud y respuesta usan JSON.
- Los errores de recurso inexistente se responden con `404 Not Found`.
- Los errores de conflicto por duplicidad o por relaciones existentes se responden con `409 Conflict`.
- Los DTOs validan los datos de entrada; las respuestas no exponen entidades JPA directamente.

## Responsabilidades por capa

- **Controller:** atiende la comunicación HTTP, recibe DTOs de solicitud, aplica la validación de entrada, usa el Mapper para convertir DTOs y entidades, invoca el Service y convierte con el Mapper el resultado a DTO de respuesta. También define rutas, parámetros y códigos de estado HTTP.
- **Service:** ejecuta la lógica de negocio y coordina la comunicación con los Repositories. No debe encargarse de HTTP ni de convertir DTOs.
- **Mapper:** convierte entre DTOs y entidades. No accede a repositories ni resuelve asociaciones; la búsqueda y asignación de entidades relacionadas corresponde al Service.
- **Repository:** realiza la persistencia y consultas, sin asumir la lógica del caso de uso.

Esta separación es la convención arquitectónica del backend. Los endpoints documentados a continuación siguen este patrón: sus Controllers convierten los DTOs con el Mapper y sus Services reciben/devuelven entidades, aplican reglas y coordinan los Repositories.

## Asignaturas

Ruta base: `/api/asignaturas`

| Método | Ruta | Descripción | Respuesta exitosa |
|---|---|---|---|
| GET | `/api/asignaturas` | Lista asignaturas ordenadas por nombre. | `200 OK`, lista de `AsignaturaResponse` |
| GET | `/api/asignaturas/{id}` | Obtiene una asignatura por ID. | `200 OK`, `AsignaturaResponse` |
| POST | `/api/asignaturas` | Crea una asignatura. | `201 Created`, `AsignaturaResponse` y encabezado `Location` |
| PUT | `/api/asignaturas/{id}` | Actualiza el nombre de una asignatura existente. | `200 OK`, `AsignaturaResponse` |
| DELETE | `/api/asignaturas/{id}` | Elimina una asignatura sin unidades asociadas. | `204 No Content` |

Solicitud de creación:

```json
{
  "nombre": "Matemática"
}
```

El nombre es obligatorio, no puede estar en blanco y admite hasta 100 caracteres. En la actualización también es obligatorio. Los nombres duplicados se rechazan con `409 Conflict`. No se puede eliminar una asignatura con unidades asociadas (`409 Conflict`).

## Recursos de contenido

Ruta base: `/api/recursos-contenido`

| Método | Ruta | Descripción | Respuesta exitosa |
|---|---|---|---|
| GET | `/api/recursos-contenido` | Lista todos los recursos; acepta el filtro opcional `?contenidoId={id}`. | `200 OK`, lista de `RecursoContenidoResponse` |
| GET | `/api/recursos-contenido/{id}` | Obtiene un recurso por ID. | `200 OK`, `RecursoContenidoResponse` |
| POST | `/api/recursos-contenido` | Crea un recurso asociado a un contenido existente. | `201 Created`, `RecursoContenidoResponse` y encabezado `Location` |
| PUT | `/api/recursos-contenido/{id}` | Actualiza tipo, URL, descripción u origen. No cambia el contenido asociado. | `200 OK`, `RecursoContenidoResponse` |
| DELETE | `/api/recursos-contenido/{id}` | Elimina un recurso. | `204 No Content` |

Solicitud de creación:

```json
{
  "contenidoId": 1,
  "tipo": "IMAGEN",
  "url": "https://ejemplo.test/imagen.png",
  "descripcion": "Representación visual del concepto",
  "origen": "PROPIO"
}
```

`contenidoId`, `tipo`, `url` y `origen` son obligatorios; `descripcion` es opcional. La URL no puede estar en blanco y ambos textos admiten hasta 500 caracteres. La creación devuelve `404 Not Found` si no existe el contenido indicado.

## Cursos

Ruta base: `/api/cursos`

| Método | Ruta | Descripción | Respuesta exitosa |
|---|---|---|---|
| GET | `/api/cursos` | Lista cursos ordenados por año y nombre. | `200 OK`, lista de `CursoResponse` |
| GET | `/api/cursos?anio={anio}` | Filtra cursos por año, ordenados por nombre. | `200 OK`, lista de `CursoResponse` |
| GET | `/api/cursos/{id}` | Obtiene un curso por ID. | `200 OK`, `CursoResponse` |
| POST | `/api/cursos` | Crea un curso. | `201 Created`, `CursoResponse` y encabezado `Location` |
| PUT | `/api/cursos/{id}` | Actualiza nombre, año o ambos; requiere al menos un campo. | `200 OK`, `CursoResponse` |
| DELETE | `/api/cursos/{id}` | Elimina un curso sin estudiantes ni asignaciones asociadas. | `204 No Content` |

Solicitud de creación:

```json
{
  "nombre": "6A",
  "anio": 2026
}
```

El nombre es obligatorio y admite hasta 50 caracteres. El año es obligatorio y debe estar entre 2000 y 2100. El filtro `anio`, cuando se envía, usa el mismo rango. Se rechazan los pares nombre-año duplicados con `409 Conflict`. No se puede eliminar un curso que tenga estudiantes o asignaciones asociadas (`409 Conflict`).

## Unidades

Ruta base: `/api/unidades`

| Método | Ruta | Descripción | Respuesta exitosa |
|---|---|---|---|
| GET | `/api/unidades` | Lista unidades ordenadas por asignatura y orden; acepta el filtro opcional `?asignaturaId={id}`. | `200 OK`, lista de `UnidadResponse` |
| GET | `/api/unidades/{id}` | Obtiene una unidad por ID. | `200 OK`, `UnidadResponse` |
| POST | `/api/unidades` | Crea una unidad asociada a una asignatura existente. | `201 Created`, `UnidadResponse` y encabezado `Location` |
| PUT | `/api/unidades/{id}` | Actualiza nivel, título, orden, estado o datos de autorización; requiere al menos un campo. | `200 OK`, `UnidadResponse` |
| DELETE | `/api/unidades/{id}` | Elimina una unidad sin objetivos, actividades, asignaciones ni informes asociados. | `204 No Content` |

Solicitud de creación:

```json
{
  "asignaturaId": 1,
  "nivelEducativo": "Sexto Basico",
  "titulo": "Fracciones",
  "orden": 1,
  "estado": "BORRADOR"
}
```

`asignaturaId`, `titulo` y `estado` son obligatorios. `nivelEducativo` y `orden` son opcionales y por defecto toman `"Sexto Basico"` y `1`. El título admite hasta 200 caracteres, el nivel hasta 50 y el orden debe ser mayor que cero. El filtro `asignaturaId` también debe ser positivo. La creación devuelve `404 Not Found` si la asignatura no existe. El filtro `asignaturaId` devuelve `404 Not Found` si la asignatura no existe.

La actualización acepta cualquier combinación no vacía de `nivelEducativo`, `titulo`, `orden`, `estado`, `autorizadaPorId` y `fechaAutorizacion`. Para cambiar el estado a `AUTORIZADO` o `PUBLICADO` se requieren ambos datos de autorización y el usuario debe tener rol `UTP`; la creación no admite esos estados porque no incluye datos de autorización. Un autorizante inexistente produce `404 Not Found`, una autorización incompleta o un usuario sin rol UTP produce `400 Bad Request`. No se puede eliminar una unidad con entidades dependientes (`409 Conflict`).

## Objetivos de aprendizaje

Ruta base: `/api/objetivos-aprendizaje`

| Método | Ruta | Descripción | Respuesta exitosa |
|---|---|---|---|
| GET | `/api/objetivos-aprendizaje` | Lista objetivos ordenados por unidad y código; acepta el filtro opcional `?unidadId={id}`. | `200 OK`, lista de `ObjetivoAprendizajeResponse` |
| GET | `/api/objetivos-aprendizaje/{id}` | Obtiene un objetivo de aprendizaje por ID. | `200 OK`, `ObjetivoAprendizajeResponse` |
| POST | `/api/objetivos-aprendizaje` | Crea un objetivo asociado a una unidad existente. | `201 Created`, `ObjetivoAprendizajeResponse` y encabezado `Location` |
| PUT | `/api/objetivos-aprendizaje/{id}` | Actualiza código, descripción, eje o texto de referencia; requiere al menos un campo. No cambia la unidad asociada. | `200 OK`, `ObjetivoAprendizajeResponse` |
| DELETE | `/api/objetivos-aprendizaje/{id}` | Elimina un objetivo sin contenidos, preguntas, asignaciones ni informes asociados. | `204 No Content` |

Solicitud de creación:

```json
{
  "unidadId": 1,
  "codigo": "OA01",
  "descripcion": "Representar fracciones propias e impropias",
  "eje": "Números",
  "textoReferencia": "Texto escolar de Matemática"
}
```

`unidadId`, `descripcion` y `eje` son obligatorios. `codigo` y `textoReferencia` son opcionales. La descripción no puede estar en blanco; el código admite hasta 50 caracteres, el eje es obligatorio y admite hasta 100 caracteres, y el texto de referencia admite hasta 500. El código, cuando se informa, debe ser único dentro de la unidad; un duplicado produce `409 Conflict`. La creación devuelve `404 Not Found` si la unidad no existe. El filtro `unidadId` debe ser positivo y también devuelve `404 Not Found` si la unidad no existe.

La actualización acepta cualquier combinación no vacía de `codigo`, `descripcion`, `eje` y `textoReferencia`; se aplican solo los valores no nulos enviados, y `descripcion` y `eje` no pueden estar en blanco. El código conserva la unicidad dentro de su unidad (`409 Conflict`). Un objetivo inexistente produce `404 Not Found`. No se puede eliminar un objetivo con contenidos, preguntas, asignaciones o detalles de informes asociados (`409 Conflict`).

## Contenidos

Ruta base: `/api/contenidos`

| Método | Ruta | Descripción | Respuesta exitosa |
|---|---|---|---|
| GET | `/api/contenidos` | Lista contenidos ordenados por objetivo de aprendizaje y orden; acepta el filtro opcional `?oaId={id}`. | `200 OK`, lista de `ContenidoResponse` |
| GET | `/api/contenidos/{id}` | Obtiene un contenido por ID. | `200 OK`, `ContenidoResponse` |
| POST | `/api/contenidos` | Crea un contenido asociado a un objetivo de aprendizaje existente. | `201 Created`, `ContenidoResponse` y encabezado `Location` |
| PUT | `/api/contenidos/{id}` | Actualiza título, explicación, ejemplos, instrucciones, orden u origen. No cambia el objetivo asociado. | `200 OK`, `ContenidoResponse` |
| DELETE | `/api/contenidos/{id}` | Elimina un contenido y sus recursos de contenido asociados. | `204 No Content` |

Solicitud de creación:

```json
{
  "oaId": 1,
  "titulo": "Representación de fracciones",
  "explicacion": "Una fracción representa partes iguales de un todo.",
  "ejemplos": "3/4 representa tres de cuatro partes iguales.",
  "instrucciones": "Observa el numerador y el denominador.",
  "orden": 1,
  "origen": "PROPIO"
}
```

`oaId`, `titulo`, `explicacion` y `origen` son obligatorios; `ejemplos`, `instrucciones` y `orden` son opcionales. El título admite hasta 200 caracteres; título y explicación no pueden estar en blanco. El orden debe ser mayor que cero y, si se omite, toma el valor `1`. `origen` admite `PROPIO`, `PUBLICO` o `AUTORIZADO`. La creación devuelve `404 Not Found` si el objetivo indicado no existe. El filtro `oaId` debe ser positivo y también devuelve `404 Not Found` si el objetivo no existe.

La actualización requiere al menos un campo no nulo. Solo se aplican los valores enviados; título y explicación no pueden estar en blanco, el orden debe ser mayor que cero y el origen debe ser uno de los valores permitidos. Un contenido inexistente produce `404 Not Found`. Al eliminar un contenido, sus recursos asociados se eliminan en cascada.

## Objetivos de aprendizaje de una asignación

Ruta base: `/api/asignaciones/{asignacionId}/objetivos-aprendizaje`

| Método | Ruta | Descripción | Respuesta exitosa |
|---|---|---|---|
| GET | `/api/asignaciones/{asignacionId}/objetivos-aprendizaje` | Lista los objetivos asociados a una asignación. | `200 OK`, lista de `AsignacionOaResponse` |
| POST | `/api/asignaciones/{asignacionId}/objetivos-aprendizaje/{oaId}` | Asocia a la asignación un objetivo de aprendizaje de su unidad. | `201 Created`, `AsignacionOaResponse` y encabezado `Location` |
| DELETE | `/api/asignaciones/{asignacionId}/objetivos-aprendizaje/{oaId}` | Quita la asociación entre la asignación y el objetivo. | `204 No Content` |

La respuesta `AsignacionOaResponse` incluye `asignacionId`, `oaId` y `unidadId`. La asociación se crea sin cuerpo de solicitud. Ambos identificadores de ruta deben ser positivos. Una asignación o un objetivo inexistente devuelve `404 Not Found`; asociar un objetivo de otra unidad devuelve `400 Bad Request`; intentar asociar nuevamente un objetivo ya asociado devuelve `409 Conflict`. Al quitar una asociación inexistente se devuelve `404 Not Found`.

## Actividades de una asignación

Ruta base: `/api/asignaciones/{asignacionId}/actividades`

| Método | Ruta | Descripción | Respuesta exitosa |
|---|---|---|---|
| GET | `/api/asignaciones/{asignacionId}/actividades` | Lista las actividades asociadas a una asignación. | `200 OK`, lista de `AsignacionActividadResponse` |
| POST | `/api/asignaciones/{asignacionId}/actividades/{actividadId}` | Asocia a la asignación una actividad de su unidad. | `201 Created`, `AsignacionActividadResponse` y encabezado `Location` |
| DELETE | `/api/asignaciones/{asignacionId}/actividades/{actividadId}` | Quita la asociación entre la asignación y la actividad. | `204 No Content` |

La respuesta `AsignacionActividadResponse` incluye `asignacionId`, `actividadId` y `unidadId`. La asociación se crea sin cuerpo de solicitud. Ambos identificadores de ruta deben ser positivos. Una asignación o actividad inexistente devuelve `404 Not Found`; asociar una actividad de otra unidad o una prueba final devuelve `400 Bad Request`; intentar asociar nuevamente una actividad ya asociada devuelve `409 Conflict`. Al quitar una asociación inexistente se devuelve `404 Not Found`.

## Alternativas

Ruta base: `/api/alternativas`

| Método | Ruta | Descripción | Respuesta exitosa |
|---|---|---|---|
| GET | `/api/alternativas` | Lista alternativas; acepta el filtro opcional `?preguntaId={id}`. | `200 OK`, lista de `AlternativaResponse` |
| GET | `/api/alternativas/{id}` | Obtiene una alternativa por ID. | `200 OK`, `AlternativaResponse` |
| POST | `/api/alternativas` | Crea una alternativa asociada a una pregunta existente. | `201 Created`, `AlternativaResponse` y encabezado `Location` |
| PUT | `/api/alternativas/{id}` | Actualiza el texto o si la alternativa es correcta. | `200 OK`, `AlternativaResponse` |
| DELETE | `/api/alternativas/{id}` | Elimina una alternativa que no haya sido seleccionada en respuestas de estudiantes. | `204 No Content` |

Solicitud de creación:

```json
{
  "preguntaId": 1,
  "texto": "Tres cuartos",
  "esCorrecta": true
}
```

`preguntaId` y `texto` son obligatorios; `esCorrecta` es opcional y por defecto toma `false`. El texto no puede estar en blanco y admite hasta 500 caracteres. Solo se admite una alternativa correcta por pregunta; intentar crear o marcar como correcta una alternativa cuando ya existe otra devuelve `409 Conflict`. La creación devuelve `404 Not Found` si la pregunta no existe. El filtro `preguntaId` debe ser positivo y devuelve `404 Not Found` si no existe la pregunta.

La actualización acepta `texto`, `esCorrecta` o ambos; requiere al menos uno y solo aplica los valores no nulos. El texto, si se envía, no puede estar en blanco y admite hasta 500 caracteres. Una alternativa inexistente produce `404 Not Found`. No se puede eliminar una alternativa usada en una respuesta de estudiante (`409 Conflict`).

## Actividades

Ruta base: `/api/actividades`

| Método | Ruta | Descripción | Respuesta exitosa |
|---|---|---|---|
| GET | `/api/actividades` | Lista actividades ordenadas por unidad y orden; acepta el filtro opcional `?unidadId={id}`. | `200 OK`, lista de `ActividadResponse` |
| GET | `/api/actividades/{id}` | Obtiene una actividad por ID. | `200 OK`, `ActividadResponse` |
| POST | `/api/actividades` | Crea una actividad asociada a una unidad existente. | `201 Created`, `ActividadResponse` y encabezado `Location` |
| PUT | `/api/actividades/{id}` | Actualiza título, tipo u orden; requiere al menos un campo. No cambia la unidad asociada. | `200 OK`, `ActividadResponse` |
| DELETE | `/api/actividades/{id}` | Elimina una actividad sin preguntas, asignaciones ni intentos asociados. | `204 No Content` |

Solicitud de creación:

```json
{
  "unidadId": 1,
  "titulo": "Explorar un texto informativo",
  "tipo": "ACTIVIDAD",
  "orden": 1
}
```

`unidadId`, `titulo` y `tipo` son obligatorios; `orden` es opcional y por defecto toma `1`. El título no puede estar en blanco y admite hasta 200 caracteres; el orden debe ser mayor que cero. `tipo` admite `ACTIVIDAD`, `DESAFIO` o `PRUEBA_FINAL`. La creación devuelve `404 Not Found` si la unidad indicada no existe. El filtro `unidadId` debe ser positivo y también devuelve `404 Not Found` si la unidad no existe.

Se rechaza con `409 Conflict` un título ya usado dentro de la unidad y una segunda `PRUEBA_FINAL` en la misma unidad (solo se admite una por unidad). La actualización acepta cualquier combinación no vacía de `titulo`, `tipo` y `orden`; el título, si se envía, no puede estar en blanco y conserva la unicidad dentro de su unidad, y `tipo` conserva la regla de una única `PRUEBA_FINAL` por unidad. Un identificador de actividad inexistente produce `404 Not Found`. No se puede eliminar una actividad con preguntas, asignaciones o intentos asociados (`409 Conflict`).

## Preguntas

Ruta base: `/api/preguntas`

| Método | Ruta | Descripción | Respuesta exitosa |
|---|---|---|---|
| GET | `/api/preguntas` | Lista preguntas ordenadas por actividad y orden; acepta los filtros opcionales `?actividadId={id}` y `?oaId={id}` (combinables). | `200 OK`, lista de `PreguntaResponse` |
| GET | `/api/preguntas/{id}` | Obtiene una pregunta por ID. | `200 OK`, `PreguntaResponse` |
| POST | `/api/preguntas` | Crea una pregunta asociada a una actividad y a un objetivo de aprendizaje existentes. | `201 Created`, `PreguntaResponse` y encabezado `Location` |
| PUT | `/api/preguntas/{id}` | Actualiza enunciado, criterio, dificultad u orden; requiere al menos un campo. No cambia la actividad, la unidad ni el objetivo asociados. | `200 OK`, `PreguntaResponse` |
| DELETE | `/api/preguntas/{id}` | Elimina una pregunta sin alternativas ni respuestas de estudiantes asociadas. | `204 No Content` |

Solicitud de creación:

```json
{
  "actividadId": 1,
  "unidadId": 1,
  "oaId": 1,
  "enunciado": "¿Qué fracción representa la figura?",
  "criterioRespuesta": "Reconocer el numerador y el denominador",
  "dificultad": "MEDIA",
  "orden": 1
}
```

`actividadId`, `unidadId`, `oaId`, `enunciado` y `dificultad` son obligatorios; `criterioRespuesta` y `orden` son opcionales (el orden por defecto toma `1`). El enunciado no puede estar en blanco y admite texto libre; el orden debe ser mayor que cero. `dificultad` admite los valores del enum `dificultad`. La creación devuelve `404 Not Found` si la actividad o el objetivo indicados no existen, y `400 Bad Request` si la actividad, la unidad y el objetivo de aprendizaje no pertenecen a la misma unidad. Los filtros `actividadId` y `oaId` deben ser positivos y devuelven `404 Not Found` si la actividad o el objetivo no existen.

La actualización acepta cualquier combinación no vacía de `enunciado`, `criterioRespuesta`, `dificultad` y `orden`; solo se aplican los valores no nulos y el enunciado, si se envía, no puede estar en blanco. Una pregunta inexistente produce `404 Not Found`. No se puede eliminar una pregunta con alternativas o respuestas de estudiantes asociadas (`409 Conflict`).

## Intentos

Ruta base: `/api/intentos`

| Método | Ruta | Descripción | Respuesta exitosa |
|---|---|---|---|
| GET | `/api/intentos` | Lista intentos ordenados por estudiante, actividad y número; acepta los filtros opcionales `?estudianteId={id}` y `?actividadId={id}` (combinables). | `200 OK`, lista de `IntentoResponse` |
| GET | `/api/intentos/{id}` | Obtiene un intento por ID. | `200 OK`, `IntentoResponse` |
| POST | `/api/estudiantes/{estudianteId}/actividades/{actividadId}/intentos` | Inicia un nuevo intento de un estudiante en una actividad. | `201 Created`, `IntentoResponse` y encabezado `Location` |
| PATCH | `/api/intentos/{id}/completar` | Marca el intento como completado fijando la fecha de fin. | `200 OK`, `IntentoResponse` |

La creación se realiza sin cuerpo de solicitud: el Service fija `fechaInicio` al instante actual y calcula `numero` como el correlativo siguiente del par estudiante-actividad. El estado de realización se deduce de `fechaFin` (nula = en progreso, con valor = completado).

Un `estudianteId` o `actividadId` inexistente devuelve `404 Not Found`. Para iniciar un intento el usuario debe tener rol `ESTUDIANTE` y curso asignado, la unidad de la actividad debe estar `AUTORIZADO` o `PUBLICADO`, y la actividad debe estar asignada al curso del estudiante (`asignacion_actividad`); en caso contrario se devuelve `400 Bad Request`. Si la actividad es `PRUEBA_FINAL`, se exige que la asignación tenga `maxIntentos` (si no, `400 Bad Request`) y que el estudiante no haya alcanzado el límite; superar el límite devuelve `409 Conflict`. Los filtros `estudianteId` y `actividadId` deben ser positivos.

`PATCH /api/intentos/{id}/completar` no recibe cuerpo. Un intento inexistente produce `404 Not Found`; completar un intento ya completado devuelve `409 Conflict`.

## Respuestas de estudiantes

Ruta base: `/api/intentos/{intentoId}/respuestas`

| Método | Ruta | Descripción | Respuesta exitosa |
|---|---|---|---|
| GET | `/api/intentos/{intentoId}/respuestas` | Lista las respuestas registradas en el intento, ordenadas por ID. | `200 OK`, lista de `RespuestaEstudianteResponse` |
| POST | `/api/intentos/{intentoId}/respuestas` | Registra la respuesta del estudiante a una pregunta del intento. | `201 Created`, `RespuestaEstudianteResponse` y encabezado `Location` |

El cuerpo de la creación incluye `preguntaId` y `alternativaId`, ambos obligatorios; `intentoId` proviene de la ruta. El Service **deriva `esCorrecta`** desde la alternativa seleccionada (foto congelada del resultado, según el modelo) y no lo recibe del cliente.

Un `intentoId`, `preguntaId` o `alternativaId` inexistente devuelve `404 Not Found`. Se devuelve `400 Bad Request` si la alternativa no pertenece a la pregunta indicada o si la pregunta no pertenece a la actividad del intento (regla 9 del modelo). Registrar una respuesta en un intento ya completado o sobre una pregunta ya respondida devuelve `409 Conflict` (unicidad `intento_id, pregunta_id`). El `intentoId` de la ruta debe ser positivo.

## Endpoints pendientes

### Informes pedagógicos

- `GET /api/informes-pedagogicos`
- `GET /api/informes-pedagogicos?estudianteId={estudianteId}`
- `GET /api/informes-pedagogicos?unidadId={unidadId}`
- `GET /api/informes-pedagogicos/{id}`
- `POST /api/informes-pedagogicos`
- `PATCH /api/informes-pedagogicos/{id}/validacion`
- `GET /api/informes-pedagogicos/{informeId}/objetivos-aprendizaje`

### Asignaciones

- `GET /api/asignaciones`
- `GET /api/asignaciones?cursoId={cursoId}`
- `GET /api/asignaciones?unidadId={unidadId}`
- `GET /api/asignaciones?docenteId={docenteId}`
- `GET /api/asignaciones/{id}`
- `POST /api/asignaciones`
- `PUT /api/asignaciones/{id}`
- `DELETE /api/asignaciones/{id}`

### Usuarios

- `GET /api/usuarios`
- `GET /api/usuarios/{id}`
- `POST /api/usuarios`
- `PUT /api/usuarios/{id}`
- `DELETE /api/usuarios/{id}`
