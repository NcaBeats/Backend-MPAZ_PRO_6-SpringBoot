# MPAZ PRO 6° · Modelo de datos v4 lean (rev. 2)

**17 tablas · 8 enums · 7 FK compuestas.** Datos 100% ficticios. Sin email (el caso no lo pide y prohíbe correos reales).

**Leyenda:** `PK` clave primaria · `FK` clave foránea · `NN` no nulo · `U` único · `?` admite nulo.
Todos los `id` son `bigint` autogenerado (identity).

---

## 1. Qué es "lean" respecto a la v4

| Recorte | Por qué |
|---|---|
| Sin `detalle_informe_oa.porcentaje` | Se calcula: `correctas * 100.0 / total_preguntas`. Guardarlo permitía contradecir a `correctas` y `total_preguntas` |
| Sin `intento.estado` ni el enum `estado_avance` | Se deduce de `fecha_fin` (ver sección 5). Guardarlo permitía `COMPLETADO` con `fecha_fin` nula |
| Sin `progreso_contenido` | Ningún requisito del caso pide saber qué explicaciones leyó el estudiante; el caso habla de **actividades**, y eso lo cubre `intento` |
| Sin `actividad.dificultad` | Era copia de la dificultad de sus preguntas y podía contradecirse |
| Sin `estudiante_id` ni `unidad_id` en `informe_pedagogico` | Se deducen desde `intento_id` |

---

## 2. Enums (8)

**Convención de almacenamiento: `varchar` + `CHECK`**, con un `enum` de Java por cada uno (`@Enumerated(EnumType.STRING)`, nunca `ORDINAL`). Evita los problemas del tipo `ENUM` nativo de PostgreSQL con Hibernate y con las herramientas de diagramas.

| Enum | Valores | Qué control del caso sostiene |
|---|---|---|
| `rol` | UTP, DOCENTE, ESTUDIANTE | Perfiles: el estudiante no ve el informe, no elige OA, no configura intentos |
| `estado_contenido` | BORRADOR, EN_REVISION, AUTORIZADO, PUBLICADO | Autorización previa de UTP |
| `tipo_actividad` | ACTIVIDAD, DESAFIO, PRUEBA_FINAL | Distingue la prueba final (límite de intentos e informe) |
| `dificultad` | BAJA, MEDIA, ALTA | "Nivel de dificultad" y desafíos progresivos |
| `origen_material` | PROPIO, PUBLICO, AUTORIZADO | Control de propiedad intelectual |
| `tipo_recurso` | IMAGEN, AUDIO | Recursos visuales y audio de apoyo |
| `estado_informe` | GENERADO, PENDIENTE_VALIDACION, VALIDADO | Validación obligatoria del docente |
| `nivel_oa` | FORTALEZA, REFUERZO | Fortalezas y contenidos que necesitan refuerzo (nombres neutros: el caso prohíbe diagnosticar) |

Ejemplo del patrón:

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
| intento_id | bigint | NN, U, FK → intento.id (intento de la prueba final de origen) |
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

## 5. Datos derivados (se calculan, no se guardan)

| Dato | Cómo se obtiene |
|---|---|
| Estado de realización del estudiante | Sin fila en `intento` = no iniciado · `fecha_fin` nula = en progreso · `fecha_fin` con valor = completado |
| Intentos utilizados | `COUNT(*)` de `intento` del estudiante en esa actividad |
| ¿Puede iniciar otro intento? | `intentos usados < asignacion.max_intentos` (y `max_intentos` no nulo) |
| Porcentaje por OA | `correctas * 100.0 / total_preguntas` |
| Estudiante del informe | `informe → intento.estudiante_id` |
| Unidad del informe | `informe → intento → actividad.unidad_id` |
| Asignatura del informe | `... → unidad.asignatura_id` |
| Curso del estudiante | `usuario.curso_id` |

**Por qué algunos derivados sí se guardan:** `respuesta_estudiante.es_correcta`, `detalle_informe_oa.correctas`, `total_preguntas` y `nivel` se podrían calcular, pero se guardan a propósito como **foto congelada**: un informe validado no debe cambiar si después se edita una alternativa o se cambia el umbral. En cambio el porcentaje y el estado de realización solo cambian si cambian sus fuentes, así que se calculan.

**Cadena del informe por OA:** `informe_pedagogico → intento → respuesta_estudiante → pregunta → objetivo_aprendizaje`, agrupando por OA.

---

## 6. Validaciones recomendadas (CHECK)

- `asignacion.max_intentos` nulo o mayor que 0
- `intento.numero` mayor que 0
- `intento.fecha_fin` nula o posterior o igual a `fecha_inicio`
- `detalle_informe_oa.correctas` entre 0 y `total_preguntas`
- Los 8 `CHECK ... IN (...)` de los enums (sección 2)
- `informe_pedagogico`: `(estado = 'VALIDADO') = (fecha_validacion IS NOT NULL)` (no puede haber un informe validado sin fecha ni al revés)
- `unidad`: `estado NOT IN ('AUTORIZADO','PUBLICADO') OR (autorizada_por_id IS NOT NULL AND fecha_autorizacion IS NOT NULL)` (no puede haber una unidad autorizada sin quién la autorizó)

**Una sola prueba final por unidad** (índice único parcial; el caso habla de "la prueba final" en singular, y sin esto `max_intentos` no sabría a cuál aplicar):

```sql
CREATE UNIQUE INDEX ON actividad (unidad_id) WHERE tipo = 'PRUEBA_FINAL';
```

Quitar si se necesitan varias pruebas finales por unidad.

## 7. Índices recomendados

PostgreSQL **no** indexa automáticamente las claves foráneas:
`actividad(unidad_id)` · `contenido(oa_id)` · `pregunta(actividad_id)` · `pregunta(oa_id)` · `respuesta_estudiante(pregunta_id)`

---

## 8. Reglas que quedan en el código (la BD no las puede expresar)

1. Solo los estudiantes tienen `curso_id`.
2. `unidad.autorizada_por_id` debe ser un usuario con rol UTP, y `asignacion.docente_id` uno con rol DOCENTE.
3. La prueba final se habilita solo si `max_intentos` tiene valor; no se permite iniciar un intento si ya se alcanzó el límite.
4. El `intento` de un `informe_pedagogico` debe ser de una actividad de tipo `PRUEBA_FINAL`.
5. El estudiante solo ve contenido de unidades `AUTORIZADO` o `PUBLICADO`, y solo **sus propios** intentos.
6. El estudiante nunca recibe el informe ni `alternativa.es_correcta` antes de responder (aquí entran los DTO).
7. El umbral fortaleza/refuerzo (ej. 70%) es una regla fija, sin IA ni diagnóstico.
8. El informe requiere validación del docente (`estado = VALIDADO`).
9. El nivel educativo es "Sexto Basico" por defecto; el límite de **una unidad por asignatura** es de alcance del MVP y se cumple con el seed, no con la estructura.
10. La `pregunta` de una `respuesta_estudiante` debe pertenecer a la actividad de su `intento`. La BD solo garantiza que la alternativa sea de esa pregunta; sin esta validación, una respuesta mal asociada **distorsiona el informe**.
11. Un estudiante solo inicia intentos de actividades asignadas a su curso (`asignacion_actividad`) y cuya unidad esté autorizada.
12. Los OA de un `detalle_informe_oa` deben pertenecer a la unidad del informe.

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

## 11. Decisiones abiertas

1. **Alcance del informe.** Este modelo asume que el informe resume **un solo intento de la prueba final**. Si debe consolidar **todas las actividades de la unidad**, hay que quitar `intento_id` y volver a guardar `estudiante_id` y `unidad_id` en `informe_pedagogico`.
2. **Contenidos asignados.** Se asignan **por OA**. Si la rúbrica exige elegirlos uno a uno, agregar `asignacion_contenido(asignacion_id, contenido_id)` con el mismo patrón que `asignacion_actividad`.
3. **Estado de realización.** Se calcula. Si la rúbrica lo exige como dato guardado, agregar `intento.estado` con `CHECK ((estado = 'COMPLETADO') = (fecha_fin IS NOT NULL))`.
4. **Docentes en varios cursos.** `usuario.curso_id` sirve para estudiantes; la relación docente-curso vive en `asignacion`. Si hace falta listar los cursos de un docente sin asignaciones, agregar una tabla `usuario_curso`.
5. **`rol`: enum o tabla.** Se mantiene como enum: son 3 perfiles fijos y un rol nuevo exige código nuevo de todos modos. Pasa a tabla solo si un usuario puede tener varios roles, si los permisos se editan como datos o si un administrador crea roles desde la app.
6. **Asignación sin filas.** Si `asignacion_oa` o `asignacion_actividad` no tienen filas para una asignación, el estudiante no ve nada (selección explícita). Es lo más simple y evita ambigüedades; el seed debe crear esas filas. Confirmar con el equipo.

---

## 12. Notas para el backend (Spring Boot + Java)

- El `.sql` es la **fuente de verdad** (Flyway); `ddl-auto: validate`, nunca `update`.
- Java 17 o superior (Spring Boot 3).
- Enums: `enum` de Java + `@Enumerated(EnumType.STRING)`, nunca `ORDINAL`. Ninguno debe ser `sealed` (son valores guardados, sin datos por variante).
- Entidades: clases normales (no `record`) y sin `@Data` de Lombok. DTO: `record`.
- `asignacion_oa` y `asignacion_actividad` tienen clave compuesta: en JPA requieren `@EmbeddedId` (clase `@Embeddable` que implementa `Serializable` con `equals` y `hashCode`).
- Las FK compuestas y los `CHECK` viven en la migración SQL, no en las anotaciones.
- Autenticación propia con Spring Security: BCrypt para `password_hash` y JWT con el rol.
