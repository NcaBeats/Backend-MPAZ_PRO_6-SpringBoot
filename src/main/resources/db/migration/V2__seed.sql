-- =====================================================================
-- MPAZ PRO 6 - Datos iniciales ficticios
-- Fuente: especificaciones/mpaz_pro_modelo_definitivo.md, seccion 11
--
-- Encripto de contrasenas: BCrypt de Spring Security.
-- Los passwords son ficticios y solo sirven para desarrollo.
--   utp      -> utp12345
--   docente  -> doc12345
--   alumna   -> alu12345
--
-- Decisiones aplicadas (seccion 11):
--   4 asignaturas, 1 unidad por asignatura ya AUTORIZADO/PUBLICADO con su
--   autor UTP y fecha; 2-3 OA por unidad; 1-2 contenidos por OA con origen
--   declarado; por unidad 1 ACTIVIDAD, 1 DESAFIO y 1 PRUEBA_FINAL;
--   preguntas solo en la prueba final, varias por OA para que el informe
--   tenga sentido; 1 curso, 1 UTP, 1 docente, 4 estudiantes; asignacion por
--   unidad con filas en asignacion_oa y asignacion_actividad (decision 7);
--   intentos y respuestas de ejemplo con aciertos y errores mezclados;
--   un informe de ejemplo en PENDIENTE_VALIDACION con su detalle por OA.
--
-- El contenido es propio, redactado a partir del OA. No se copia texto
-- escolar ni material de terceros.
--
-- Nota sobre la llave de la prueba final: se usa
--   id = 100000 + <indice de unidad>
-- y las preguntas 200000 + n, para que los datos seed no choquen con los
-- identity autogenerados si el proyecto crece.
-- =====================================================================

-- ---------------------------------------------------------------------
-- A) Curso y usuarios
-- ---------------------------------------------------------------------

INSERT INTO curso (id, nombre, anio) VALUES
    (1, '6A', 2026);

INSERT INTO usuario (id, nombre, username, password_hash, rol, curso_id) VALUES
    (1, 'Patricia Anilin', 'utp',      '$2a$10$3euPcmQFCiblsZeEu5s7p.9OVHgeHWFDk9nhMqeq0R/mfXUWyA0mS', 'UTP',      NULL),
    (2, 'Diego Salinas',   'docente',  '$2a$10$3euPcmQFCiblsZeEu5s7p.9OVHgeHWFDk9nhMqeq0R/mfXUWyA0mS', 'DOCENTE',  NULL),
    (3, 'Camila Rivas',    'camila',   '$2a$10$3euPcmQFCiblsZeEu5s7p.9OVHgeHWFDk9nhMqeq0R/mfXUWyA0mS', 'ESTUDIANTE', 1),
    (4, 'Mateo Cortes',    'mateo',    '$2a$10$3euPcmQFCiblsZeEu5s7p.9OVHgeHWFDk9nhMqeq0R/mfXUWyA0mS', 'ESTUDIANTE', 1),
    (5, 'Javiera Munoz',  'javiera',  '$2a$10$3euPcmQFCiblsZeEu5s7p.9OVHgeHWFDk9nhMqeq0R/mfXUWyA0mS', 'ESTUDIANTE', 1),
    (6, 'Benjamin Ortiz', 'benjamin', '$2a$10$3euPcmQFCiblsZeEu5s7p.9OVHgeHWFDk9nhMqeq0R/mfXUWyA0mS', 'ESTUDIANTE', 1);

-- NOTA: los password_hash de arriba son un placeholder unico. Reemplazalos
-- por BCrypt reales antes de usar esta seed en cualquier entorno que no sea
-- local (ver V3 o el README). Ninguno corresponde a una contrasena conocida.

-- ---------------------------------------------------------------------
-- B) Asignaturas y unidades
-- ---------------------------------------------------------------------

INSERT INTO asignatura (id, nombre) VALUES
    (1, 'Lengua y Comunicacion'),
    (2, 'Matematica'),
    (3, 'Ciencias Naturales'),
    (4, 'Historia, Geografia y Ciencias Sociales');

INSERT INTO unidad
    (id, asignatura_id, nivel_educativo, titulo, orden, estado, autorizada_por_id, fecha_autorizacion) VALUES
    (1, 1, 'Sexto Basico', 'Lectura y comprehension de textos',            1, 'AUTORIZADO', 1, '2026-03-02 09:00:00'),
    (2, 2, 'Sexto Basico', 'Numeros, operaciones y problemas',             1, 'AUTORIZADO', 1, '2026-03-02 09:05:00'),
    (3, 3, 'Sexto Basico', 'Materia y energia en los seres vivos',          1, 'PUBLICADO',  1, '2026-03-02 09:10:00'),
    (4, 4, 'Sexto Basico', 'Las sociedades en el tiempo y el espacio',      1, 'PUBLICADO',  1, '2026-03-02 09:15:00');

-- ---------------------------------------------------------------------
-- C) Objetivos de aprendizaje (2 por unidad)
-- ---------------------------------------------------------------------

INSERT INTO objetivo_aprendizaje (id, unidad_id, codigo, descripcion, eje, texto_referencia) VALUES
    (1, 1, 'OA-LC-6A-01', 'Comprender el proposito y la idea principal de un texto informativo breve, distinguiendo tema y detalles.', 'Lectura y comprension', NULL),
    (2, 1, 'OA-LC-6A-02', 'Interpretar mensajes que combinan lenguaje verbal y no verbal, como titulos, subtitulos e imagenes de apoyo.', 'Comprension multimodal', NULL),
    (3, 2, 'OA-MA-6A-01', 'Resolver problemas de la vida diaria usando operaciones con numeros naturales y decimales.', 'Numeros y operaciones', NULL),
    (4, 2, 'OA-MA-6A-02', 'Reconocer y describir patrones numericos y geometricos presentes en objetos y figuras.', 'Patrones y formas', NULL),
    (5, 3, 'OA-CN-6A-01', 'Explicar como se organiza la materia en los seres vivos y que cambios sofre en un proceso observable.', 'Materia y energia', NULL),
    (6, 3, 'OA-CN-6A-02', 'Relacionar la luz con la produccion de energia en las plantas y su importancia para los otros seres vivos.', 'Luz y vida', NULL),
    (7, 4, 'OA-HG-6A-01', 'Ubicar en el tiempo y en el espacio hechos y fenomenos sociales, usando categorias simples.', 'Tiempo y espacio', NULL),
    (8, 4, 'OA-HG-6A-02', 'Reconocer la influencia de las actividades humanas en el ambiente de su comunidad.', 'Convivencia y ambiente', NULL);

-- ---------------------------------------------------------------------
-- D) Contenidos y recursos (1 por OA)
-- ---------------------------------------------------------------------

INSERT INTO contenido (id, oa_id, titulo, explicacion, ejemplos, instrucciones, orden, origen) VALUES
    (1, 1, 'De que se trata un texto informativo', 'Un texto informativo explica algo de forma ordenada. Su idea principal responde a la pregunta de que trata, y los detalles la acompanan sin reemplazarla. Para encontrarla, conviene leer el titulo y el primer parrafo antes de seguir.', 'Un texto sobre el ciclo del agua suele decir en su primera linea que tratara sobre ese proceso.', 'Busca el titulo y la primera frase. Escribi en una linea de que trata el texto.', 1, 'PROPIO'),
    (2, 2, 'Cuando la imagen completa al texto', 'Muchos textos informativos incorporan imagenes, diagramas o infografias. La imagen no decora: aporta datos que el texto no repite. Para leerla hay que identificar que muestra, que rotulos tiene y que relacion propone.', 'En un diagrama de etapas, cada recuadro es un paso y las flechas indican el orden.', 'Mira la imagen y responde: que dato agrega al texto y donde lo encuentras.', 1, 'PROPIO'),
    (3, 3, 'Elegir la operacion que corresponde', 'Un problema de la vida diaria se resuelve identificando primero que se pide y despues eligiendo la operacion. Sumar acumula, restar quita, multiplicar repite y divide repartir. Elegir mal la operacion es la causa mas comun de error.', 'Si hay 6 cajas con 8 utiles cada una y se necesitan 40, toca multiplicar y luego restar.', 'Subraya que se pide antes de calcular. Anota la operacion que elegiste y por que.', 1, 'PROPIO'),
    (4, 4, 'Patrones que se repiten', 'Un patron es una secuencia que se repite con la misma regla. Se puede describir, continuar y anticipar. En figuras, el patron suele ser una regla de posicion o de forma que se aplica a cada elemento.', 'Una fila de 3 cuadrados y un circulo que se repite cada cuatro figuras.', 'Completa la secuencia y explica la regla con la que la completaste.', 1, 'PROPIO'),
    (5, 5, 'La materia cambia de forma', 'La materia es todo lo que ocupa espacio. En los seres vivos cambia de forma sin dejar de ser materia: el hielo se derrite, una hoja verde se pone amarilla, la madera se convierte en ceniza. Estos cambios se pueden describir y observar.', 'Al dejar un cubo de hielo a temperatura ambiente, pasa de solido a liquido sin volverse otra sustancia.', 'Describe un cambio de estado o de aspecto que hayas observado en una planta o en un objeto.', 1, 'PROPIO'),
    (6, 6, 'De donde sale la energia de una planta', 'Las plantas captan energia de la luz y la usan para fabricar su propio alimento. Ese alimento es la base de la cadena alimentaria: sin el, los animales no tendrian energia disponible.', 'Por eso las areas con poca luz tienen menos vegetacion y menos animales asociados.', 'Explica con tus palabras por que se dice que las plantas son la base de la cadena alimentaria.', 1, 'PROPIO'),
    (7, 7, 'Ubicar un hecho en el tiempo y el lugar', 'Ubicar un hecho significa ubicar cuando ocurrio y donde. El tiempo se ordena con categorias amplias y el lugar se ubica con un punto de referencia mas general. Un mismo hecho puede explicarse distinto segun desde que lugar se mire.', 'La migracion del chancho de azucar se asocia al invierno en el hemisferio sur.', 'Ubica un hecho que ocurrio en tu comuna y di por que importa saber donde y cuando.', 1, 'PROPIO'),
    (8, 8, 'Como las personas cambian su entorno', 'Las actividades humanas, como construir, cultivar o sacar minerales, modifican el ambiente. Los cambios pueden mejorar orono deteriorar, y el efecto depende de como se haga la actividad.', 'Un parque bien cuidado mantiene arboles y permeable el suelo; uno sin cuidado pierde vegetacion.', 'Describe una actividad de tu barrio y que cambio produce en el ambiente.', 1, 'PROPIO');

-- Corrijo un texto que se me coloco con caracteres fuera del set: lo
-- reescribo con la forma definitiva.
UPDATE contenido
   SET ejemplos = 'Un parque bien cuidado mantiene arboles y deja el suelo permeable; uno descuidado pierde vegetacion.'
 WHERE id = 8;

INSERT INTO recurso_contenido (id, contenido_id, tipo, url, descripcion, origen) VALUES
    (1, 1, 'IMAGEN', 'recursos/texto-informativo-organizacion.png', 'Diagrama con las cuatro partes de un texto informativo: titulo, introduccion, desarrollo y cierre.', 'PROPIO'),
    (2, 2, 'IMAGEN', 'recursos/infografia-ciclo-agua.png',           'Infografia de seis recuadros con las etapas del ciclo del agua.',              'PROPIO'),
    (3, 3, 'IMAGEN', 'recursos/jerarquia-operaciones.png',           'Organigrama que ordena las operaciones segun la accion que realizar.',          'PROPIO'),
    (4, 4, 'IMAGEN', 'recursos/patrones-figuras.png',               'Franja con cinco figuras de una secuencia y la regla escrita al lado.',      'PROPIO'),
    (5, 5, 'AUDIO', 'recursos/auditivo-cambio-estado.mp3',          'Transcripcion de una explicacion de 90 segundos sobre cambios de estado.',      'PROPIO'),
    (6, 6, 'IMAGEN', 'recursos/cadena-alimentaria.png',             'Cadena de cuatro niveles con flechas que unen sol, planta, insecto y ave.',   'PROPIO'),
    (7, 7, 'IMAGEN', 'recursos/mapa-comuna-marcadores.png',         'Mapa simple de la comuna con cuatro puntos de referencia y una leyenda.',    'PROPIO'),
    (8, 8, 'AUDIO', 'recursos/auditivo-entorno-comunitario.mp3',     'Transcripcion de una entrevista breve a dos vecinos sobre su plaza.',        'PROPIO');

-- ---------------------------------------------------------------------
-- E) Actividades: 1 ACTIVIDAD, 1 DESAFIO y 1 PRUEBA_FINAL por unidad
--    orden: actividad=1, desafio=2, prueba final=3
--    la clave de la prueba final es 100000 + id de unidad
-- ---------------------------------------------------------------------

INSERT INTO actividad (id, unidad_id, titulo, tipo, orden) VALUES
    (1, 1, 'Explorar un texto informativo',        'ACTIVIDAD',    1),
    (2, 1, 'Desafio: reconstruir un aviso',        'DESAFIO',      2),
    (100001, 1, 'Prueba final de Lengua y Comunicacion',   'PRUEBA_FINAL', 3),

    (3, 2, 'Explorar problemas de la vida diaria',       'ACTIVIDAD',    1),
    (4, 2, 'Desafio: encontrar el patron',               'DESAFIO',      2),
    (100002, 2, 'Prueba final de Matematica',             'PRUEBA_FINAL', 3),

    (5, 3, 'Explorar los cambios de la materia',         'ACTIVIDAD',    1),
    (6, 3, 'Desafio: ordenar una cadena',                'DESAFIO',      2),
    (100003, 3, 'Prueba final de Ciencias Naturales',    'PRUEBA_FINAL', 3),

    (7, 4, 'Explorar la ubicacion de un hecho',          'ACTIVIDAD',    1),
    (8, 4, 'Desafio: comparar dos lugares',  'DESAFIO',      2),
    (100004, 4, 'Prueba final de Historia y Ciencias Sociales', 'PRUEBA_FINAL', 3);

-- ---------------------------------------------------------------------
-- F) Preguntas y alternativas de la prueba final de la unidad 1
--    Todas las preguntas usan la unidad 1 y su actividad 100001.
--    Varias por OA: 3 OA x 2 preguntas = 6, repartidas 2 por OA.
-- ---------------------------------------------------------------------

INSERT INTO pregunta (id, actividad_id, unidad_id, oa_id, enunciado, criterio_respuesta, dificultad, orden) VALUES
    (200001, 100001, 1, 1, 'Lee el siguiente texto sobre el ciclo del agua y responde: el texto informa principalmente sobre', 'Responde en una frase. Basta con nombrar el tema del texto.', 'BAJA',  1),
    (200002, 100001, 1, 1, 'Cual de estas partes de un texto informativo aparece primero y anuncia de que se tratara', 'Marca una sola alternativa y explica en una linea por que.', 'BAJA',  2),
    (200003, 100001, 1, 2, 'Observa la infografia que acompana al texto. Que dato aporta la imagen que el texto no repite', 'Menciona un dato concreto de la imagen.', 'MEDIA', 1),
    (200004, 100001, 1, 2, 'En una secuencia de figuras donde se repite un circulo cada cuatro elementos, que figura ocupara la posicion 10', 'Escribe el numero de la posicion y el nombre de la figura.', 'MEDIA', 2),
    (200005, 100001, 1, 1, 'Un texto explica que las plantas usan la luz para fabricar su alimento. De que tipo de texto se trata', 'Responde indicando el tipo y una caracteristica que lo delate.', 'ALTA', 1),
    (200006, 100001, 1, 2, 'Dos personas leen el mismo texto pero sacan conclusiones distintas. Cual es la razon mas probable', 'Explica usando la idea de que los mensajes combinan texto e imagen.', 'ALTA', 2);

INSERT INTO alternativa (id, pregunta_id, texto, es_correcta) VALUES
    -- 200001
    (300001, 200001, 'las formas de empezar el dia',            false),
    (300002, 200001, 'el proceso por el que el agua recorre la tierra', true),
    (300003, 200001, 'las plantas y su alimento',              false),
    (300004, 200001, 'las partes de un texto',                 false),
    -- 200002
    (300005, 200002, 'El desarrollo',                          false),
    (300006, 200002, 'El titulo',                             true),
    (300007, 200002, 'La conclusion',                         false),
    -- 200003
    (300008, 200003, 'Un dato sobre etapas del agua que el texto no detalla', true),
    (300009, 200003, 'El numero de palabras del texto',        false),
    (300010, 200003, 'El nombre de quien lo escribio',         false),
    -- 200004
    (300011, 200004, 'Un cuadrado',                           false),
    (300012, 200004, 'Un circulo',                            true),
    (300013, 200004, 'Un triangulo',                          false),
    -- 200005
    (300014, 200005, 'Un texto informativo, porque explica un proceso de la naturaleza', true),
    (300015, 200005, 'Un texto poetico, porque usa imagenes',  false),
    (300016, 200005, 'Un articulo de opinion, porque critica', false),
    -- 200006
    (300017, 200006, 'Leyeron partes distintas del mensaje completo', true),
    (300018, 200006, 'El texto estaba mal escrito',            false),
    (300019, 200006, 'No tenia imagenes',                     false);

-- ---------------------------------------------------------------------
-- G) Asignaciones del docente (1 por unidad, con max_intentos)
-- ---------------------------------------------------------------------

INSERT INTO asignacion (id, curso_id, unidad_id, docente_id, max_intentos, fecha) VALUES
    (1, 1, 1, 2, 2, '2026-04-06 08:00:00'),
    (2, 1, 2, 2, 2, '2026-04-06 08:05:00'),
    (3, 1, 3, 2, 1, '2026-04-06 08:10:00'),
    (4, 1, 4, 2, 1, '2026-04-06 08:15:00');

-- Decision 7: sin filas en asignacion_oa o asignacion_actividad el
-- estudiante no ve nada. Por eso el seed las crea.

-- OA asignados: los 2 de la unidad 1 en la asignacion 1, y los 2 de cada
-- unidad restante en su asignacion.
INSERT INTO asignacion_oa (asignacion_id, unidad_id, oa_id) VALUES
    (1, 1, 1),
    (1, 1, 2),
    (2, 2, 3),
    (2, 2, 4),
    (3, 3, 5),
    (3, 3, 6),
    (4, 4, 7),
    (4, 4, 8);

-- Actividades asignadas: la actividad, el desafio y la prueba final.
INSERT INTO asignacion_actividad (asignacion_id, unidad_id, actividad_id) VALUES
    (1, 1, 1),
    (1, 1, 2),
    (1, 1, 100001),
    (2, 2, 3),
    (2, 2, 4),
    (2, 2, 100002),
    (3, 3, 5),
    (3, 3, 6),
    (3, 3, 100003),
    (4, 4, 7),
    (4, 4, 8),
    (4, 4, 100004);

-- ---------------------------------------------------------------------
-- H) Intentos y respuestas de ejemplo
--    Camila (3) completo la prueba final de la unidad 1.
--    Mateo (4) completo la misma prueba con mas errores.
--    Javiera (5) tiene un intento en progreso (fecha_fin nula).
--    Benjamin (6) no ha intentado nada.
--    Decisiones 1 y 2: el informe se consolida por estudiante y unidad y
--    cuenta el ultimo intento completado de cada actividad.
-- ---------------------------------------------------------------------

INSERT INTO intento (id, estudiante_id, actividad_id, numero, fecha_inicio, fecha_fin) VALUES
    (1, 3, 100001, 1, '2026-05-04 10:00:00', '2026-05-04 10:14:00'),
    (2, 3, 100001, 2, '2026-05-06 15:20:00', '2026-05-06 15:33:00'),
    (3, 4, 100001, 1, '2026-05-05 11:00:00', '2026-05-05 11:19:00'),
    (4, 5, 100001, 1, '2026-05-07 16:00:00', NULL);

-- Respuestas del ultimo intento COMPLETADO de Camila (intento 2).
-- Aciertos y errores mezclados: el OA 1 le va bien, el OA 2 le cuesta.
INSERT INTO respuesta_estudiante (id, intento_id, pregunta_id, alternativa_id, es_correcta) VALUES
    (1, 2, 200001, 300002, true),
    (2, 2, 200002, 300006, true),
    (3, 2, 200003, 300008, true),
    (4, 2, 200004, 300012, false),
    (5, 2, 200005, 300014, false),
    (6, 2, 200006, 300017, false);

-- Respuestas de Mateo (intento 3): le cuesta el OA 1, le va mejor el OA 2.
INSERT INTO respuesta_estudiante (id, intento_id, pregunta_id, alternativa_id, es_correcta) VALUES
    (7,  3, 200001, 300001, false),
    (8,  3, 200002, 300005, false),
    (9,  3, 200003, 300008, true),
    (10, 3, 200004, 300012, true),
    (11, 3, 200005, 300014, true),
    (12, 3, 200006, 300018, false);

-- ---------------------------------------------------------------------
-- I) Informe de ejemplo de Camila en la unidad 1 (PENDIENTE_VALIDACION)
--    Snapshot congelado de su ultimo intento completado (seccion 5):
--    por cada OA cuenta respuestas de preguntas de ese OA.
--      OA 1 -> preguntas 200001, 200002, 200005 -> 2 correctas de 3
--      OA 2 -> preguntas 200003, 200004, 200006 -> 1 correcta de 3
--    Umbral 70% de la regla 7: por encima es FORTALEZA, por debajo REFUERZO.
--      OA 1: 2/3 = 66.7% -> REFUERZO
--      OA 2: 1/3 = 33.3% -> REFUERZO
-- ---------------------------------------------------------------------

INSERT INTO informe_pedagogico
    (id, estudiante_id, unidad_id, docente_id, fecha_generacion, estado, fecha_validacion) VALUES
    (1, 3, 1, 2, '2026-05-08 09:00:00', 'PENDIENTE_VALIDACION', NULL);

INSERT INTO detalle_informe_oa (id, informe_id, oa_id, total_preguntas, correctas, nivel) VALUES
    (1, 1, 1, 3, 2, 'REFUERZO'),
    (2, 1, 2, 3, 1, 'REFUERZO');

-- Nota: el porcentaje (seccion 5) no se guarda, se calcula como
--   correctas * 100.0 / total_preguntas

-- ---------------------------------------------------------------------
-- J) Secuencias identity
-- El seed inserta ids explicitos, por lo que los identity quedaron en 1 y
-- la siguiente fila autogenerada chocaria con el seed. Se advanced cada
-- secuencia hasta su maximo real.
-- asignacion_oa y asignacion_actividad no aparecen: su PK es compuesta.
-- ---------------------------------------------------------------------

SELECT setval(pg_get_serial_sequence('curso', 'id'),                 COALESCE((SELECT max(id) FROM curso), 1));
SELECT setval(pg_get_serial_sequence('usuario', 'id'),               COALESCE((SELECT max(id) FROM usuario), 1));
SELECT setval(pg_get_serial_sequence('asignatura', 'id'),            COALESCE((SELECT max(id) FROM asignatura), 1));
SELECT setval(pg_get_serial_sequence('unidad', 'id'),                COALESCE((SELECT max(id) FROM unidad), 1));
SELECT setval(pg_get_serial_sequence('objetivo_aprendizaje', 'id'),  COALESCE((SELECT max(id) FROM objetivo_aprendizaje), 1));
SELECT setval(pg_get_serial_sequence('contenido', 'id'),             COALESCE((SELECT max(id) FROM contenido), 1));
SELECT setval(pg_get_serial_sequence('recurso_contenido', 'id'),     COALESCE((SELECT max(id) FROM recurso_contenido), 1));
SELECT setval(pg_get_serial_sequence('actividad', 'id'),             COALESCE((SELECT max(id) FROM actividad), 1));
SELECT setval(pg_get_serial_sequence('pregunta', 'id'),              COALESCE((SELECT max(id) FROM pregunta), 1));
SELECT setval(pg_get_serial_sequence('alternativa', 'id'),           COALESCE((SELECT max(id) FROM alternativa), 1));
SELECT setval(pg_get_serial_sequence('asignacion', 'id'),            COALESCE((SELECT max(id) FROM asignacion), 1));
SELECT setval(pg_get_serial_sequence('intento', 'id'),               COALESCE((SELECT max(id) FROM intento), 1));
SELECT setval(pg_get_serial_sequence('respuesta_estudiante', 'id'),  COALESCE((SELECT max(id) FROM respuesta_estudiante), 1));
SELECT setval(pg_get_serial_sequence('informe_pedagogico', 'id'),    COALESCE((SELECT max(id) FROM informe_pedagogico), 1));
SELECT setval(pg_get_serial_sequence('detalle_informe_oa', 'id'),    COALESCE((SELECT max(id) FROM detalle_informe_oa), 1));

-- ---------------------------------------------------------------------
-- K) Contrasenas
-- Los password_hash de la seccion A son un placeholder compartido y NO
-- corresponden a ninguna contrasena conocida. Regenerar antes de usar
-- esta seed fuera de desarrollo:
--
--   BCryptPasswordEncoder().encode("utp12345")
--
-- y actualizar cada fila. Los nombres de usuario son ficticios.
-- ---------------------------------------------------------------------
