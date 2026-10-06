# MPAZ PRO 6° · Modelo de datos definitivo

**17 tablas · 8 enums · 7 FK compuestas.** Base: PostgreSQL. Datos 100% ficticios. Sin email (el caso no lo pide y prohíbe correos reales).

**Leyenda:** `PK` clave primaria · `FK` clave foránea · `NN` no nulo · `U` único · `?` admite nulo.
Todos los `id` son `bigint` autogenerado (identity).

> Este documento reemplaza a `mpaz_pro_v4_lean.md` y a las versiones v3.x. La arquitectura (con o sin backend) y el lenguaje de la app quedan fuera de este documento.

---

## 1. Decisiones tomadas

| # | Decisión | Resolución | Cómo revertir |
|---|---|---|---|
| 1 | Alcance del informe | **Consolidado por estudiante y unidad**, no por intento. El caso lo identifica por "Estudiante ficticio" y "Asignatura y unidad" (3.2), habla de un "informe consolidado" (2.4) y de "historial de rendimiento" por OA | Si la rúbrica dice que sale solo de la prueba final: agregar `intento_id` y quitar `estudiante_id` y `unidad_id` |
| 2 | Qué intento cuenta en el informe | **El último intento completado** de cada actividad | Es regla de código: cambiar a "el mejor" no toca la base |
| 3 | Contenidos asignados | **Por OA**: al elegir un OA, el estudiante ve todos sus contenidos | Si la rúbrica pide elegirlos uno a uno: tabla `asignacion_contenido(asignacion_id, unidad_id, contenido_id)`, mismo patrón que `asignacion_actividad` |
| 4 | Estado de realización | **Calculado** desde `fecha_fin` | Si se exige guardado: `intento.estado` + `CHECK ((estado = 'COMPLETADO') = (fecha_fin IS NOT NULL))` |
| 5 | Docente y cursos | La relación docente-curso vive en `asignacion` | Si se necesita listar cursos de un docente sin asignaciones: tabla `usuario_curso` |
| 6 | `rol` | **Enum** (3 perfiles fijos; un rol nuevo exige código nuevo de todos modos) | Tabla solo si un usuario puede tener varios roles o los permisos se editan como datos |
| 7 | Asignación sin filas | Si `asignacion_oa` o `asignacion_actividad` no tienen filas, el estudiante **no ve nada**. El seed debe crear esas filas | Regla de código |
| 8 | Enums | `varchar` + `CHECK`, no el `ENUM` nativo de PostgreSQL | Evita problemas con Hibernate y con herramientas de diagramas |
| 9 | `asignatura` | **Tabla** (agregar una asignatura es insertar una fila) | Volver a enum si se prefiere simplicidad |
| 10 | `nivel_educativo` | Texto en `unidad`, por defecto "Sexto Basico" | |
| 11 | Pruebas finales | **Una por unidad** (índice único parcial) | Quitar el índice si se necesitan varias |
| 12 | Unidades | El límite de **una unidad por asignatura** es de alcance del MVP: se cumple con el seed, no con la estructura | Cargar más unidades sin cambiar nada |

### Qué se recortó respecto a modelos anteriores

| Recorte | Por qué |
|---|---|
| Sin `progreso_contenido` | Ningún requisito pide saber qué explicaciones leyó el estudiante; el caso habla de **actividades**, y eso lo cubre `intento` |
| Sin `intento.estado` ni enum `estado_avance` | Se deduce de `fecha_fin` |
| Sin `detalle_informe_oa.porcentaje` | Se calcula: `correctas * 100.0 / total_preguntas` |
| Sin `actividad.dificultad` | Era copia de la dificultad de sus preguntas y podía contradecirse |
| Sin tabla `rol`, `nivel_educativo` ni `desafio`/`prueba` separadas | Son etiquetas (`rol`, `tipo`), no entidades |

---

## 2. Enums (8)

Cada uno se guarda como `varchar` con un `CHECK ... IN (...)`.

| Enum | Valores | Qué control del caso sostiene |
|---|---|---|
| `rol` | UTP, DOCENTE, ESTUDIANTE | Perfiles: el estudiante no ve el informe, no elige OA, no configura intentos |
| `estado_contenido` | BORRADOR, EN_REVISION, AUTORIZADO, PUBLICADO | Autorización previa de UTP |
| `tipo_actividad` | ACTIVIDAD, DESAFIO, PRUEBA_FINAL | Distingue la prueba final (límite de intentos) |
| `dificultad` | BAJA, MEDIA, ALTA | "Nivel de dificultad" y desafíos progresivos |
| `origen_material` | PROPIO, PUBLICO, AUTORIZADO | Control de propiedad intelectual |
| `tipo_recurso` | IMAGEN, AUDIO | Recursos visuales y audio de apoyo |
| `estado_informe` | GENERADO, PENDIENTE_VALIDACION, VALIDADO | Validación obligatoria del docente |
| `nivel_oa` | FORTALEZA, REFUERZO | Fortalezas y contenidos que necesitan refuerzo (nombres neutros: el caso prohíbe diagnosticar) |

```sql
estado varchar NOT NULL DEFAULT 'BORRADOR'
  CHECK (estado IN ('BORRADOR','EN_REVISION','AUTORIZADO','PUBLICADO'))
```

---

## 3. Tablas (17)

### A) Usuarios

**usuario**
| Columna | Tipo | Restricciones |
|---|---|---|
| id | bigint | PK |
| nombre | varchar | NN (ficticio) |
| username | varchar | NN, U |
| password_hash | varchar | NN (nunca la contraseña en texto) |
| rol | varchar | NN, enum `rol` |
| curso_id | bigint | ?, FK → curso.id. Solo estudiantes |

**curso**
| Columna | Tipo | Restricciones |
|---|---|---|
| id | bigint | PK |
| nombre | varchar | NN (ej. "6A") |
| anio | int | NN (ej. 2026) |

### B) Catálogo curricular

**asignatura**
| Columna | Tipo | Restricciones |
|---|---|---|
| id | bigint | PK |
| nombre | varchar | NN, U |

**unidad**
| Columna | Tipo | Restricciones |
|---|---|---|
| id | bigint | PK |
| asignatura_id | bigint | NN, FK → asignatura.id |
| nivel_educativo | varchar | NN, default 'Sexto Basico' |
| titulo | varchar | NN |
| orden | int | NN, default 1 |
| estado | varchar | NN, default 'BORRADOR', enum `estado_contenido` |
| autorizada_por_id | bigint | ?, FK → usuario.id (debe ser UTP) |
| fecha_autorizacion | timestamp | ? |

**objetivo_aprendizaje**
| Columna | Tipo | Restricciones |
|---|---|---|
| id | bigint | PK |
| unidad_id | bigint | NN, FK → unidad.id |
| codigo | varchar | ? (código oficial, "cuando corresponda") |
| descripcion | text | NN |
| eje | varchar | NN |
| texto_referencia | varchar | ? (texto escolar o recurso oficial) |

Únicos: `(id, unidad_id)` y `(unidad_id, codigo)`.

**contenido**
| Columna | Tipo | Restricciones |
|---|---|---|
| id | bigint | PK |
| oa_id | bigint | NN, FK → objetivo_aprendizaje.id (la unidad se deduce por el OA) |
| titulo | varchar | NN |
| explicacion | text | NN |
| ejemplos | text | ? |
| instrucciones | text | ? |
| orden | int | NN, default 1 |
| origen | varchar | NN, enum `origen_material` |

**recurso_contenido**
| Columna | Tipo | Restricciones |
|---|---|---|
| id | bigint | PK |
| contenido_id | bigint | NN, FK → contenido.id |
| tipo | varchar | NN, enum `tipo_recurso` |
| url | varchar | NN |
| descripcion | varchar | ? (texto alternativo o transcripción) |
| origen | varchar | NN, enum `origen_material` |

**actividad** (incluye actividades, desafíos y prueba final)
| Columna | Tipo | Restricciones |
|---|---|---|
| id | bigint | PK |
| unidad_id | bigint | NN, FK → unidad.id |
| titulo | varchar | NN |
| tipo | varchar | NN, enum `tipo_actividad` |
| orden | int | NN, default 1 |

Único: `(id, unidad_id)`.

**pregunta**
| Columna | Tipo | Restricciones |
|---|---|---|
| id | bigint | PK |
| actividad_id | bigint | NN, FK → actividad.id |
| unidad_id | bigint | NN (copia deliberada de la unidad de la actividad) |
| oa_id | bigint | NN, FK → objetivo_aprendizaje.id (**base del informe por OA**) |
| enunciado | text | NN |
| criterio_respuesta | text | ? |
| dificultad | varchar | NN, enum `dificultad` |
| orden | int | NN, default 1 |

**alternativa**
| Columna | Tipo | Restricciones |
|---|---|---|
| id | bigint | PK |
| pregunta_id | bigint | NN, FK → pregunta.id |
| texto | varchar | NN |
| es_correcta | boolean | NN, default false |

Único: `(pregunta_id, id)`.

### C) Control del docente

**asignacion**
| Columna | Tipo | Restricciones |
|---|---|---|
| id | bigint | PK |
| curso_id | bigint | NN, FK → curso.id |
| unidad_id | bigint | NN, FK → unidad.id |
| docente_id | bigint | NN, FK → usuario.id (debe ser DOCENTE) |
| max_intentos | int | ? (**nulo = prueba final no habilitada**) |
| fecha | timestamp | NN |

Únicos: `(curso_id, unidad_id)` y `(id, unidad_id)`.

**asignacion_oa** (OA que el docente selecciona; los contenidos se asignan por OA)
| Columna | Tipo | Restricciones |
|---|---|---|
| asignacion_id | bigint | NN, FK → asignacion.id |
| unidad_id | bigint | NN |
| oa_id | bigint | NN, FK → objetivo_aprendizaje.id |

**PK compuesta única: `(asignacion_id, oa_id)`** (una sola clave de dos columnas, no dos claves).

**asignacion_actividad** (desafíos y actividades que asigna el docente)
| Columna | Tipo | Restricciones |
|---|---|---|
| asignacion_id | bigint | NN, FK → asignacion.id |
| unidad_id | bigint | NN |
| actividad_id | bigint | NN, FK → actividad.id |

**PK compuesta única: `(asignacion_id, actividad_id)`**.

### D) Actividad del estudiante

**intento**
| Columna | Tipo | Restricciones |
|---|---|---|
| id | bigint | PK |
| estudiante_id | bigint | NN, FK → usuario.id |
| actividad_id | bigint | NN, FK → actividad.id |
| numero | int | NN |
| fecha_inicio | timestamp | NN |
| fecha_fin | timestamp | ? (confirma que la actividad se completó) |

Único: `(estudiante_id, actividad_id, numero)`.

**respuesta_estudiante**
| Columna | Tipo | Restricciones |
|---|---|---|
| id | bigint | PK |
| intento_id | bigint | NN, FK → intento.id |
| pregunta_id | bigint | NN, FK → pregunta.id |
| alternativa_id | bigint | NN, FK → alternativa.id |
| es_correcta | boolean | NN (derivado, se guarda a propósito: congela el resultado si se edita la alternativa) |

Único: `(intento_id, pregunta_id)`.

### E) Informe (solo docente)

**informe_pedagogico**
| Columna | Tipo | Restricciones |
|---|---|---|
| id | bigint | PK |
| estudiante_id | bigint | NN, FK → usuario.id (debe ser ESTUDIANTE) |
| unidad_id | bigint | NN, FK → unidad.id (la asignatura se deduce de la unidad) |
| docente_id | bigint | NN, FK → usuario.id (docente responsable) |
| fecha_generacion | timestamp | NN |
| estado | varchar | NN, default 'GENERADO', enum `estado_informe` |
| fecha_validacion | timestamp | ? |

**detalle_informe_oa**
| Columna | Tipo | Restricciones |
|---|---|---|
| id | bigint | PK |
| informe_id | bigint | NN, FK → informe_pedagogico.id |
| oa_id | bigint | NN, FK → objetivo_aprendizaje.id |
| total_preguntas | int | NN |
| correctas | int | NN |
| nivel | varchar | NN, enum `nivel_oa` |

Único: `(informe_id, oa_id)`.

---

## 4. FK compuestas (7)

Cada una exige que la columna referenciada tenga el **único compuesto** indicado en su tabla.

| Tabla y columnas | Referencia | Qué impide |
|---|---|---|
| pregunta `(actividad_id, unidad_id)` | actividad `(id, unidad_id)` | Pregunta con unidad distinta a la de su actividad |
| pregunta `(oa_id, unidad_id)` | objetivo_aprendizaje `(id, unidad_id)` | Pregunta con OA de otra unidad |
| asignacion_oa `(asignacion_id, unidad_id)` | asignacion `(id, unidad_id)` | OA asignado en otra unidad |
| asignacion_oa `(oa_id, unidad_id)` | objetivo_aprendizaje `(id, unidad_id)` | Ídem |
| asignacion_actividad `(asignacion_id, unidad_id)` | asignacion `(id, unidad_id)` | Actividad asignada en otra unidad |
| asignacion_actividad `(actividad_id, unidad_id)` | actividad `(id, unidad_id)` | Ídem |
| respuesta_estudiante `(pregunta_id, alternativa_id)` | alternativa `(pregunta_id, id)` | Alternativa de otra pregunta |

---

## 5. Datos derivados

| Dato | Cómo se obtiene |
|---|---|
| Estado de realización | Sin fila en `intento` = no iniciado · `fecha_fin` nula = en progreso · `fecha_fin` con valor = completado |
| Intentos utilizados | `COUNT(*)` de `intento` del estudiante en esa actividad |
| ¿Puede iniciar otro intento? | `intentos usados < asignacion.max_intentos` (y `max_intentos` no nulo) |
| Porcentaje por OA | `correctas * 100.0 / total_preguntas` |
| Asignatura del informe | `informe.unidad_id → unidad.asignatura_id` |
| Intento que cuenta por actividad | El último `intento` completado del estudiante en esa actividad (decisión 2) |
| Curso del estudiante | `usuario.curso_id` |

**Cadena del informe por OA:** se toman las `respuesta_estudiante` de los intentos del estudiante (`intento.estudiante_id`) en actividades de la unidad (`actividad.unidad_id`), se pasa por `pregunta` hasta `objetivo_aprendizaje` y se agrupa por OA. El resultado se congela en `detalle_informe_oa`.

**Por qué algunos derivados sí se guardan:** `respuesta_estudiante.es_correcta`, `detalle_informe_oa.correctas`, `total_preguntas` y `nivel` se podrían calcular, pero se guardan a propósito como **foto congelada**: un informe validado no debe cambiar si después se edita una alternativa o se cambia el umbral. El porcentaje y el estado de realización solo cambian si cambian sus fuentes, así que se calculan.

---

## 6. Validaciones (CHECK) e índice parcial

- `asignacion.max_intentos` nulo o mayor que 0
- `intento.numero` mayor que 0
- `intento.fecha_fin` nula o posterior o igual a `fecha_inicio`
- `detalle_informe_oa.correctas` entre 0 y `total_preguntas`
- Los 8 `CHECK ... IN (...)` de los enums (sección 2)
- `informe_pedagogico`: `(estado = 'VALIDADO') = (fecha_validacion IS NOT NULL)`
- `unidad`: `estado NOT IN ('AUTORIZADO','PUBLICADO') OR (autorizada_por_id IS NOT NULL AND fecha_autorizacion IS NOT NULL)`

**Una sola prueba final por unidad** (sin esto, `max_intentos` no sabría a cuál aplicar):

```sql
CREATE UNIQUE INDEX ON actividad (unidad_id) WHERE tipo = 'PRUEBA_FINAL';
```

## 7. Índices de consulta

PostgreSQL **no** indexa automáticamente las claves foráneas:

`actividad(unidad_id)` · `contenido(oa_id)` · `pregunta(actividad_id)` · `pregunta(oa_id)` · `respuesta_estudiante(pregunta_id)` · `informe_pedagogico(estudiante_id, unidad_id)`

---

## 8. Reglas que quedan en el código (la BD no las puede expresar)

1. Solo los estudiantes tienen `curso_id`, y `informe_pedagogico.estudiante_id` debe ser un ESTUDIANTE.
2. `unidad.autorizada_por_id` debe ser UTP; `asignacion.docente_id` e `informe_pedagogico.docente_id` deben ser DOCENTE.
3. La prueba final se habilita solo si `max_intentos` tiene valor; no se permite iniciar un intento si ya se alcanzó el límite (el límite aplica solo a la `PRUEBA_FINAL`).
4. El informe se genera con las respuestas del estudiante en las actividades de esa unidad, tomando el último intento completado de cada una. Solo debe existir informe si hay intentos completados.
5. El estudiante solo ve contenido de unidades `AUTORIZADO` o `PUBLICADO`, solo lo asignado a su curso (por OA y actividad; asignación sin filas = nada) y solo **sus propios** intentos.
6. El estudiante nunca recibe el informe ni `alternativa.es_correcta` antes de responder (aquí entran los DTO).
7. El umbral fortaleza/refuerzo (ej. 70%) es una regla fija, sin IA. La app no diagnostica ni aplica consecuencias académicas automáticas.
8. El informe requiere validación del docente (`estado = VALIDADO`).
9. La `pregunta` de una `respuesta_estudiante` debe pertenecer a la actividad de su `intento`. La BD solo garantiza que la alternativa sea de esa pregunta; sin esta validación, una respuesta mal asociada **distorsiona el informe**.
10. Un estudiante solo inicia intentos de actividades asignadas a su curso y cuya unidad esté autorizada.
11. Los OA de un `detalle_informe_oa` deben pertenecer a la unidad del informe.

---

## 9. Requisito del caso → dónde queda

| Requisito | Dónde |
|---|---|
| Unidad, estado y autorización de UTP | `unidad` |
| OA con código oficial opcional, eje y texto de referencia | `objetivo_aprendizaje` |
| Explicaciones, ejemplos, instrucciones y origen | `contenido` |
| Imágenes (varias), audio y su origen | `recurso_contenido` |
| Actividades, desafíos y prueba final | `actividad.tipo` |
| Pregunta, alternativas, correcta o criterio, dificultad, OA | `pregunta`, `alternativa` |
| Docente selecciona OA | `asignacion_oa` |
| Docente asigna desafíos | `asignacion_actividad` |
| Límite de intentos antes de habilitar la prueba | `asignacion.max_intentos` |
| Intentos utilizados y estado de realización | `intento` |
| Respuestas correctas e incorrectas | `respuesta_estudiante` |
| Rendimiento, fortalezas y refuerzo por OA | `detalle_informe_oa` |
| Estado del informe y docente responsable | `informe_pedagogico` |
| Curso ficticio | `curso` |
| Informe oculto para el estudiante | Regla 6 (DTO y roles) |
| Sin IA ni diagnóstico | Regla 7 |

---

## 10. Orden de creación

1. `curso`, `asignatura`
2. `usuario`
3. `unidad`
4. `objetivo_aprendizaje`, `actividad`
5. `contenido`, `recurso_contenido`, `pregunta`, `alternativa`
6. `asignacion`, `asignacion_oa`, `asignacion_actividad`
7. `intento`, `respuesta_estudiante`
8. `informe_pedagogico`, `detalle_informe_oa`

---

## 11. Seed (datos iniciales ficticios)

| Qué | Cantidad sugerida |
|---|---|
| Asignaturas | 4 (Lenguaje y Comunicación, Matemática, Ciencias Naturales, Historia, Geografía y Ciencias Sociales) |
| Unidades | 1 por asignatura, en estado `AUTORIZADO` o `PUBLICADO` con autor UTP y fecha |
| OA por unidad | 2 a 3 (con código oficial cuando corresponda) |
| Contenidos por OA | 1 a 2, con `origen` declarado |
| Actividades por unidad | 1 actividad, 1 desafío y 1 prueba final |
| Preguntas | 4 a 6 en la prueba final, **varias por OA** para que el informe tenga sentido |
| Usuarios | 1 UTP, 1 docente, 3 a 5 estudiantes (nombres y credenciales ficticios) |
| Curso | 1 (ficticio) |
| Asignación | 1 por unidad, con `max_intentos` fijado y sus filas en `asignacion_oa` y `asignacion_actividad` |
| Intentos y respuestas | Algunos de ejemplo, con aciertos y errores mezclados |
| Informe | Uno de ejemplo en estado `PENDIENTE_VALIDACION` |

**El contenido debe ser propio o público.** No copiar páginas del texto escolar: escribir explicaciones y preguntas propias a partir del OA oficial.

---

## 12. Notas si el proyecto usa backend Spring Boot + Java

- Java 17 o superior (Spring Boot 3).
- El `.sql` es la **fuente de verdad** (Flyway, `V1__esquema.sql` y `V2__seed.sql`); `ddl-auto: validate`, nunca `update`. Una migración ya ejecutada no se edita: se agrega una nueva.
- Enums: `enum` de Java + `@Enumerated(EnumType.STRING)`, nunca `ORDINAL`.
- Entidades: clases normales (no `record`) y sin `@Data` de Lombok. DTO: `record`.
- `asignacion_oa` y `asignacion_actividad` tienen clave compuesta: `@EmbeddedId` (clase `@Embeddable` que implementa `Serializable` con `equals` y `hashCode`).
- Las FK compuestas y los `CHECK` viven en la migración SQL. Las entidades deben llenar siempre `unidad_id` en `pregunta`, `asignacion_oa` y `asignacion_actividad`, o la base rechaza el guardado.
- Autenticación propia con Spring Security: BCrypt para `password_hash` y JWT con el rol. Autorizar **por rol y por dueño** (un estudiante solo ve sus datos).

---

## 13. Plan de trabajo

Construye **un flujo completo con una asignatura** antes de ampliar a las otras tres.

| Fase | Entregable | Listo cuando |
|---|---|---|
| 1 | `V1__esquema.sql` | Se ejecuta en PostgreSQL sin errores y rechaza datos inválidos |
| 2 | `V2__seed.sql` con una unidad completa | Puedes consultar un informe con datos |
| 3 | Lógica de negocio: las reglas de la sección 8 y la generación del informe | Cada regla tiene una implementación |
| 4 | Pruebas de las reglas críticas | Hay tests para el límite de intentos, el acceso por rol y el cálculo del informe |
| 5 | Un flujo completo de punta a punta: login → contenido → prueba → resultado → informe del docente | Funciona con una asignatura |
| 6 | Las otras 3 asignaturas y el APK firmado en modo release | Corre en un teléfono real |

### Riesgos conocidos

1. **El APK debe ser release y firmado.** Genera el keystore pronto y guárdalo; si lo pierdes, no puedes volver a firmar.
2. **Seguridad por rol y por dueño.** Que el login funcione no basta: el estudiante no debe ver `es_correcta` antes de responder, el informe ni intentos ajenos.
3. **Un informe mal calculado es el fallo más visible.** Prueba la cadena respuesta → pregunta → OA con datos conocidos antes de construir pantallas.
4. **Datos 100% ficticios y contenido propio.** Ni nombres, RUT, correos o notas reales, ni material protegido.
