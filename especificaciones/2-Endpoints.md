# Endpoints implementados

Este documento resume las API REST implementadas hasta el momento para Asignatura, RecursoContenido, Curso, Unidad, ObjetivoAprendizaje y Contenido.

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
