-- ============================================================
-- V6: datos de demostracion
--
-- Replica los usuarios, catalogos y relaciones base que usa la app
-- Android. La contrasena de todos los usuarios es `mpaz123` y se guarda
-- como hash BCrypt.
--
-- Los ids son explicitos para que las relaciones del seed sean estables.
-- Al final se reajustan las secuencias para que los inserts nuevos no
-- choquen con los ids sembrados.
-- ============================================================

-- 1. Roles
INSERT INTO rol (rol_id, nombre) VALUES
    (1, 'UTP'),
    (2, 'DOCENTE'),
    (3, 'ESTUDIANTE');

-- 2. Usuarios (hash BCrypt de mpaz123)
INSERT INTO usuario (usuario_id, pnombre, snombre, apaterno, amaterno, nombre_usuario, password_hash, rol_id, activo) VALUES
    (1, 'Carlos', NULL, 'UTP', NULL, 'utp', '$2y$10$G9eiaV79wF9balaZIMR6z.SQEgip0iNPGlKi/4oPMB18GmqLvGBL6', 1, TRUE),
    (2, 'Ana', NULL, 'Silva', NULL, 'docente', '$2y$10$G9eiaV79wF9balaZIMR6z.SQEgip0iNPGlKi/4oPMB18GmqLvGBL6', 2, TRUE),
    (3, 'Bruno', NULL, 'Morales', NULL, 'docente2', '$2y$10$G9eiaV79wF9balaZIMR6z.SQEgip0iNPGlKi/4oPMB18GmqLvGBL6', 2, TRUE),
    (4, 'Carla', NULL, 'Rojas', NULL, 'docente3', '$2y$10$G9eiaV79wF9balaZIMR6z.SQEgip0iNPGlKi/4oPMB18GmqLvGBL6', 2, TRUE),
    (5, 'Daniel', NULL, 'Vargas', NULL, 'docente4', '$2y$10$G9eiaV79wF9balaZIMR6z.SQEgip0iNPGlKi/4oPMB18GmqLvGBL6', 2, TRUE),
    (6, 'Elena', NULL, 'Castro', NULL, 'alumno', '$2y$10$G9eiaV79wF9balaZIMR6z.SQEgip0iNPGlKi/4oPMB18GmqLvGBL6', 3, TRUE),
    (7, 'Felipe', NULL, 'Gómez', NULL, 'estudiante2', '$2y$10$G9eiaV79wF9balaZIMR6z.SQEgip0iNPGlKi/4oPMB18GmqLvGBL6', 3, TRUE),
    (8, 'Gabriela', NULL, 'Herrera', NULL, 'estudiante3', '$2y$10$G9eiaV79wF9balaZIMR6z.SQEgip0iNPGlKi/4oPMB18GmqLvGBL6', 3, TRUE),
    (9, 'Hugo', NULL, 'Ibarra', NULL, 'estudiante4', '$2y$10$G9eiaV79wF9balaZIMR6z.SQEgip0iNPGlKi/4oPMB18GmqLvGBL6', 3, TRUE);

-- 3. Nivel y secciones
INSERT INTO nivel (nivel_id, nombre) VALUES (1, 'Sexto Básico');
INSERT INTO seccion (seccion_id, letra) VALUES (1, 'A'), (2, 'B');

-- 4. Cursos
INSERT INTO curso (curso_id, annio, nivel_id, seccion_id) VALUES
    (1, 2026, 1, 1),
    (2, 2026, 1, 2);

-- 5. Estados de autorizacion
INSERT INTO estado_autorizacion (estado_id, nombre) VALUES
    (1, 'BORRADOR'),
    (2, 'EN_REVISION'),
    (3, 'AUTORIZADO'),
    (4, 'PUBLICADO');

-- 6. Tipos de actividad y recurso
INSERT INTO tipo_actividad (tipo_actividad_id, nombre) VALUES
    (1, 'ACTIVIDAD'),
    (2, 'DESAFIO'),
    (3, 'PRUEBA_FINAL');

INSERT INTO tipo_recurso (tipo_recurso_id, nombre) VALUES
    (1, 'IMAGEN'),
    (2, 'AUDIO'),
    (3, 'DOCUMENTO'),
    (4, 'VIDEO');

-- 7. Asignaturas y unidades
INSERT INTO asignatura (asignatura_id, nombre, nivel_id) VALUES
    (1, 'Lenguaje y Comunicación', 1),
    (2, 'Matemática', 1),
    (3, 'Ciencias Naturales', 1),
    (4, 'Historia, Geografía y Ciencias Sociales', 1);

INSERT INTO unidad (unidad_id, nombre, asignatura_id, estado_id) VALUES
    (1, 'Lectura - Comprensión', 1, 2),
    (2, 'Números y operaciones', 2, 3),
    (3, 'Ciencias de la Vida', 3, 2),
    (4, 'Historia', 4, 4);

-- 8. Docentes por curso/asignatura
INSERT INTO asignatura_curso (id_asignaturas_curso, curso_id, asignatura_id, docente_id) VALUES
    (1, 1, 1, 2),
    (2, 1, 2, 3),
    (3, 1, 3, 4),
    (4, 1, 4, 5),
    (5, 2, 1, 2),
    (6, 2, 2, 3),
    (7, 2, 3, 4),
    (8, 2, 4, 5);

-- 9. Participantes por curso
INSERT INTO usuario_curso (usuario_curso_id, usuario_id, curso_id) VALUES
    (1, 2, 1), (2, 2, 2),
    (3, 3, 1), (4, 3, 2),
    (5, 4, 1), (6, 4, 2),
    (7, 5, 1), (8, 5, 2),
    (9, 6, 1), (10, 7, 1),
    (11, 8, 2), (12, 9, 2);

-- 10. Objetivos de aprendizaje y su oferta por unidad
INSERT INTO objetivo_aprendizaje (objetivo_aprendizaje_id, descripcion, codigo) VALUES
    (1, 'Leer de manera fluida textos variados apropiados para la edad.', 'LE06 OA 01'),
    (2, 'Comprender textos aplicando estrategias de comprensión lectora.', 'LE06 OA 02'),
    (3, 'Demostrar que comprenden los factores y múltiplos.', 'MA06 OA 01'),
    (4, 'Realizar cálculos con las cuatro operaciones en la resolución de problemas.', 'MA06 OA 02'),
    (5, 'Explicar los requerimientos de agua, dióxido de carbono y energía lumínica para la fotosíntesis.', 'CN06 OA 01'),
    (6, 'Representar la transferencia de energía y materia en cadenas y redes alimentarias.', 'CN06 OA 02'),
    (7, 'Explicar los antecedentes de la independencia de las colonias americanas.', 'HI06 OA 01'),
    (8, 'Explicar el desarrollo del proceso de independencia de Chile.', 'HI06 OA 02');

INSERT INTO objetivo_aprendizaje_unidad (oa_unidad_id, objetivo_aprendizaje_id, unidad_id) VALUES
    (1, 1, 1),
    (2, 2, 1),
    (3, 3, 2),
    (4, 4, 2),
    (5, 5, 3),
    (6, 6, 3),
    (7, 7, 4),
    (8, 8, 4);

-- 11. Contenidos
INSERT INTO contenido (contenido_id, descripcion, estado_id, unidad_id) VALUES
    (1, 'Estrategias para comprender lo que leemos: activar conocimientos previos, identificar información explícita, inferir y resumir.', 2, 1),
    (2, 'Números y operaciones: múltiplos, factores, números primos y descomposición en factores primos.', 3, 2),
    (3, 'La fotosíntesis y las cadenas alimentarias: cómo las plantas producen su alimento y cómo fluye la energía.', 2, 3),
    (4, 'Antecedentes de la independencia de América y Chile: Ilustración, independencia de EE.UU., Revolución francesa e invasión napoleónica.', 4, 4);

INSERT INTO objetivo_aprendizaje_contenido (oa_contenido_id, contenido_id, objetivo_aprendizaje_id, unidad_id) VALUES
    (1, 1, 1, 1),
    (2, 1, 2, 1),
    (3, 2, 3, 2),
    (4, 2, 4, 2),
    (5, 3, 5, 3),
    (6, 3, 6, 3),
    (7, 4, 7, 4),
    (8, 4, 8, 4);

-- 12. Recursos de contenido (obligatorios para preguntas y alternativas)
INSERT INTO recurso (recurso_id, nombre, url, descripcion, tipo_recurso_id, contenido_id) VALUES
    (1, 'Guía de comprensión lectora', NULL, 'Material de apoyo del contenido.', 3, 1),
    (2, 'Guía de números y operaciones', NULL, 'Material de apoyo del contenido.', 3, 2),
    (3, 'Guía de fotosíntesis', NULL, 'Material de apoyo del contenido.', 3, 3),
    (4, 'Guía de independencia', NULL, 'Material de apoyo del contenido.', 3, 4);

-- 13. Actividades
INSERT INTO actividad (actividad_id, instrucciones, tipo_actividad_id, contenido_id, estado_id) VALUES
    (1, 'Lee cada pregunta y marca la alternativa correcta.', 1, 1, 2),
    (2, 'Selecciona la alternativa correcta en cada pregunta de múltiplos y factores.', 1, 2, 3),
    (3, 'Evaluación final de la unidad de números y operaciones.', 3, 2, 3),
    (4, 'Responde las preguntas sobre la fotosíntesis y las redes alimentarias.', 1, 3, 2),
    (5, 'Responde las preguntas sobre los antecedentes de la independencia.', 1, 4, 4);

-- 14. Publicación de actividades por curso
INSERT INTO actividad_curso (actividad_curso_id, actividad_id, curso_id, fecha_inicio, fecha_fin, habilitada, visible, max_intentos) VALUES
    (1, 1, 1, NULL, NULL, TRUE, TRUE, 3),
    (2, 1, 2, NULL, NULL, TRUE, TRUE, 3),
    (3, 2, 1, NULL, NULL, TRUE, TRUE, 3),
    (4, 2, 2, NULL, NULL, TRUE, TRUE, 3),
    (5, 3, 1, NULL, NULL, TRUE, TRUE, 3),
    (6, 3, 2, NULL, NULL, TRUE, TRUE, 3),
    (7, 4, 1, NULL, NULL, TRUE, TRUE, 3),
    (8, 4, 2, NULL, NULL, TRUE, TRUE, 3),
    (9, 5, 1, NULL, NULL, TRUE, TRUE, 3),
    (10, 5, 2, NULL, NULL, TRUE, TRUE, 3);

-- 15. Preguntas
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES
    (1, '¿Cuál de los siguientes números es múltiplo de 7?', 2),
    (2, '¿Cuál de las siguientes alternativas contiene todos los divisores de 18?', 2),
    (3, '¿Cuál de los siguientes números es primo?', 2),
    (4, '¿Cuál es la descomposición correcta de 30 en factores primos?', 2),
    (5, '¿Qué proceso permite a las plantas producir su alimento?', 3),
    (6, '¿Qué organismo produce su propio alimento en una cadena alimentaria?', 3),
    (7, '¿Qué acontecimiento de 1808 provocó una crisis de la monarquía española?', 4),
    (8, '¿Qué idea de la Ilustración cuestionaba el poder absoluto del rey?', 4),
    (9, '¿Qué estrategia ayuda a comprender un texto antes de leerlo?', 1);

-- 16. Alternativas (4 por pregunta; la correcta se marca con TRUE)
INSERT INTO alternativa (alternativa_id, enunciado, recurso_id, correcta) VALUES
    (1, '14', 2, TRUE), (2, '15', 2, FALSE), (3, '18', 2, FALSE), (4, '20', 2, FALSE),
    (5, '1, 2, 3, 6, 9 y 18', 2, TRUE), (6, '1, 2, 4, 8 y 18', 2, FALSE), (7, '1, 3, 5, 9 y 18', 2, FALSE), (8, '1, 2, 6, 12 y 18', 2, FALSE),
    (9, '9', 2, FALSE), (10, '11', 2, TRUE), (11, '15', 2, FALSE), (12, '21', 2, FALSE),
    (13, '2 × 3 × 5', 2, TRUE), (14, '2 × 2 × 5', 2, FALSE), (15, '3 × 3 × 5', 2, FALSE), (16, '2 × 5 × 5', 2, FALSE),
    (17, 'La fotosíntesis', 3, TRUE), (18, 'La digestión', 3, FALSE), (19, 'La respiración', 3, FALSE), (20, 'La evaporación', 3, FALSE),
    (21, 'Los productores', 3, TRUE), (22, 'Los consumidores', 3, FALSE), (23, 'Los descomponedores', 3, FALSE), (24, 'Los depredadores', 3, FALSE),
    (25, 'La invasión napoleónica a España', 4, TRUE), (26, 'La independencia de Estados Unidos', 4, FALSE), (27, 'La Revolución francesa', 4, FALSE), (28, 'La independencia de Haití', 4, FALSE),
    (29, 'La soberanía nacional', 4, TRUE), (30, 'El derecho divino de los reyes', 4, FALSE), (31, 'El mercantilismo', 4, FALSE), (32, 'La servidumbre', 4, FALSE),
    (33, 'Observar el título y activar conocimientos previos', 1, TRUE), (34, 'Copiar el texto sin leerlo', 1, FALSE), (35, 'Leer solo la última línea', 1, FALSE), (36, 'Ignorar las imágenes', 1, FALSE);

-- 17. Asociaciones pregunta-alternativa
INSERT INTO alternativa_pregunta (alternativa_pregunta_id, pregunta_id, alternativa_id) VALUES
    (1, 1, 1), (2, 1, 2), (3, 1, 3), (4, 1, 4),
    (5, 2, 5), (6, 2, 6), (7, 2, 7), (8, 2, 8),
    (9, 3, 9), (10, 3, 10), (11, 3, 11), (12, 3, 12),
    (13, 4, 13), (14, 4, 14), (15, 4, 15), (16, 4, 16),
    (17, 5, 17), (18, 5, 18), (19, 5, 19), (20, 5, 20),
    (21, 6, 21), (22, 6, 22), (23, 6, 23), (24, 6, 24),
    (25, 7, 25), (26, 7, 26), (27, 7, 27), (28, 7, 28),
    (29, 8, 29), (30, 8, 30), (31, 8, 31), (32, 8, 32),
    (33, 9, 33), (34, 9, 34), (35, 9, 35), (36, 9, 36);

-- 18. Asociaciones pregunta-actividad
INSERT INTO pregunta_actividad (pregunta_actividad_id, actividad_id, pregunta_id) VALUES
    (1, 2, 1),
    (2, 2, 2),
    (3, 2, 3),
    (4, 3, 4),
    (5, 4, 5),
    (6, 4, 6),
    (7, 5, 7),
    (8, 5, 8),
    (9, 1, 9);

-- 19. Un intento cerrado de ejemplo para el estudiante 6 (curso 1, actividad 2)
INSERT INTO intento (intento_id, fecha_inicio, fecha_fin, usuario_id, actividad_curso_id) VALUES
    (1, '2026-11-01T10:00:00Z', '2026-11-01T10:15:00Z', 6, 3);

INSERT INTO respuesta (respuesta_id, alternativa_id, pregunta_id, intento_id) VALUES
    (1, 1, 1, 1),
    (2, 5, 2, 1),
    (3, 10, 3, 1);

-- 20. Informe pedagogico de ejemplo para el estudiante 6 en la unidad 2
INSERT INTO informe_pedagogico (informe_pedagogico_id, usuario_id, fecha, descripcion, unidad_id, fecha_corte, origen) VALUES
    (1, 6, '2026-11-02T09:00:00Z', 'Informe de ejemplo generado a partir de los resultados de la unidad.', 2, '2026-11-02T09:00:00Z', 'AUTOMATICO');

INSERT INTO detalle_informe_pedagogico (detalle_ip_id, informe_pedagogico_id, objetivo_aprendizaje_id, correctas, incorrectas) VALUES
    (1, 1, 3, 3, 0),
    (2, 1, 4, 0, 0);

-- 21. Reajustar secuencias para que los ids nuevos continuen despues del seed
DO $$
DECLARE
    tabla text;
    columna text;
    maximo bigint;
    tiene_filas boolean;
BEGIN
    FOR tabla, columna IN
        SELECT * FROM (VALUES
            ('rol', 'rol_id'),
            ('usuario', 'usuario_id'),
            ('nivel', 'nivel_id'),
            ('seccion', 'seccion_id'),
            ('curso', 'curso_id'),
            ('estado_autorizacion', 'estado_id'),
            ('tipo_actividad', 'tipo_actividad_id'),
            ('tipo_recurso', 'tipo_recurso_id'),
            ('asignatura', 'asignatura_id'),
            ('unidad', 'unidad_id'),
            ('asignatura_curso', 'id_asignaturas_curso'),
            ('usuario_curso', 'usuario_curso_id'),
            ('objetivo_aprendizaje', 'objetivo_aprendizaje_id'),
            ('objetivo_aprendizaje_unidad', 'oa_unidad_id'),
            ('contenido', 'contenido_id'),
            ('objetivo_aprendizaje_contenido', 'oa_contenido_id'),
            ('recurso', 'recurso_id'),
            ('actividad', 'actividad_id'),
            ('actividad_curso', 'actividad_curso_id'),
            ('pregunta', 'pregunta_id'),
            ('alternativa', 'alternativa_id'),
            ('alternativa_pregunta', 'alternativa_pregunta_id'),
            ('pregunta_actividad', 'pregunta_actividad_id'),
            ('intento', 'intento_id'),
            ('respuesta', 'respuesta_id'),
            ('informe_pedagogico', 'informe_pedagogico_id'),
            ('detalle_informe_pedagogico', 'detalle_ip_id')
        ) AS t(tabla, columna)
    LOOP
        EXECUTE format('SELECT COALESCE(MAX(%I), 0), COUNT(*) > 0 FROM %I', columna, tabla)
            INTO maximo, tiene_filas;
        PERFORM setval(
            pg_get_serial_sequence(tabla, columna),
            GREATEST(maximo, 1),
            tiene_filas
        );
    END LOOP;
END
$$;
