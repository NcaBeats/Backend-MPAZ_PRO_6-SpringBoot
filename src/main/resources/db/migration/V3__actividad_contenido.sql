-- =====================================================================
-- MPAZ PRO 6 - V3: actividad asociada opcionalmente a un contenido
-- Fuente: especificaciones/mpaz_pro_modelo_definitivo.md (secciones 3, 4 y 7)
--
-- Hasta V2 una actividad colgaba solo de la unidad. Ahora puede apuntar
-- ademas a un contenido de esa misma unidad (para agrupar actividades por
-- contenido) o quedar sin contenido: actividad transversal de la unidad,
-- como la prueba final, que evalua todos sus OA.
--
-- La regla "el contenido es de la misma unidad que la actividad" queda en la
-- base con la FK compuesta:
--     actividad (contenido_id, unidad_id) -> contenido (id, unidad_id)
-- Para sostenerla, contenido copia la unidad de su OA en unidad_id y declara
-- UNIQUE (id, unidad_id), igual que ya hacen objetivo_aprendizaje y
-- actividad. Esa copia no puede contradecir al OA porque ademas se declara:
--     contenido (oa_id, unidad_id) -> objetivo_aprendizaje (id, unidad_id)
--
-- Nota de conflicto de la FK compuesta: si contenido_id es NULL la FK no se
-- evalua (MATCH SIMPLE), que es justo lo que necesita la actividad
-- transversal. Con contenido_id informado, la unidad de la actividad y la
-- del contenido tienen que coincidir.
--
-- Mecanica: SQLite no acepta ALTER TABLE ... ADD CONSTRAINT ni ADD COLUMN
-- con UNIQUE/NOT NULL, asi que contenido y actividad se reconstruyen (crear,
-- copiar, borrar, renombrar). Borrar una tabla referenciada exige
-- PRAGMA foreign_keys=OFF, y ese pragma se ignora dentro de una transaccion:
-- por eso esta migracion se acompana de V3__actividad_contenido.sql.conf con
-- executeInTransaction=false (V1 y V2 siguen en transaccion). Se vuelve a
-- activar al final para que la app arranque con las FK encendidas.
-- =====================================================================

PRAGMA foreign_keys = OFF;

-- ---------------------------------------------------------------------
-- 1) contenido: unidad_id copiada del OA + UNIQUE (id, unidad_id)
-- ---------------------------------------------------------------------

CREATE TABLE contenido_nuevo (
    id            INTEGER PRIMARY KEY,
    oa_id         integer       NOT NULL,
    unidad_id     integer       NOT NULL,
    titulo        varchar(200) NOT NULL,
    explicacion   text         NOT NULL,
    ejemplos       text,
    instrucciones text,
    orden         integer      NOT NULL DEFAULT 1,
    origen        varchar(20)  NOT NULL,
    -- Sostiene la FK compuesta actividad (contenido_id, unidad_id).
    CONSTRAINT uk_contenido_id_unidad UNIQUE (id, unidad_id),
    CONSTRAINT ck_contenido_orden CHECK (orden > 0),
    CONSTRAINT ck_contenido_origen
        CHECK (origen IN ('PROPIO', 'PUBLICO', 'AUTORIZADO')),
    CONSTRAINT fk_contenido_oa
        FOREIGN KEY (oa_id) REFERENCES objetivo_aprendizaje (id),
    -- La unidad copiada tiene que ser la del OA.
    CONSTRAINT fk_contenido_oa_unidad
        FOREIGN KEY (oa_id, unidad_id)
        REFERENCES objetivo_aprendizaje (id, unidad_id)
);

INSERT INTO contenido_nuevo (id, oa_id, unidad_id, titulo, explicacion,
                              ejemplos, instrucciones, orden, origen)
SELECT c.id, c.oa_id, oa.unidad_id, c.titulo, c.explicacion,
       c.ejemplos, c.instrucciones, c.orden, c.origen
FROM contenido c
JOIN objetivo_aprendizaje oa ON oa.id = c.oa_id;

DROP TABLE contenido;
ALTER TABLE contenido_nuevo RENAME TO contenido;

-- Los indices se van con la tabla borrada; se recrean igual que en V1.
CREATE INDEX ix_contenido_oa ON contenido (oa_id);

-- ---------------------------------------------------------------------
-- 2) actividad: contenido_id opcional + FK compuesta con la unidad
-- ---------------------------------------------------------------------

CREATE TABLE actividad_nuevo (
    id           INTEGER PRIMARY KEY,
    unidad_id    integer       NOT NULL,
    contenido_id integer,
    titulo       varchar(200) NOT NULL,
    tipo         varchar(20)  NOT NULL,
    orden        integer      NOT NULL DEFAULT 1,
    -- (id, unidad_id) existe para sostener las FK compuestas de la seccion 4.
    CONSTRAINT uk_actividad_id_unidad UNIQUE (id, unidad_id),
    CONSTRAINT ck_actividad_tipo
        CHECK (tipo IN ('ACTIVIDAD', 'DESAFIO', 'PRUEBA_FINAL')),
    CONSTRAINT ck_actividad_orden CHECK (orden > 0),
    CONSTRAINT fk_actividad_unidad
        FOREIGN KEY (unidad_id) REFERENCES unidad (id),
    CONSTRAINT fk_actividad_contenido
        FOREIGN KEY (contenido_id) REFERENCES contenido (id),
    -- Seccion 4: el contenido debe pertenecer a la unidad de la actividad.
    CONSTRAINT fk_actividad_contenido_unidad
        FOREIGN KEY (contenido_id, unidad_id)
        REFERENCES contenido (id, unidad_id)
);

-- Las actividades existentes quedan transversales (contenido_id NULL); el
-- bloque 3 liga algunas para que la demo muestre el agrupamiento.
INSERT INTO actividad_nuevo (id, unidad_id, contenido_id, titulo, tipo, orden)
SELECT id, unidad_id, NULL, titulo, tipo, orden
FROM actividad;

DROP TABLE actividad;
ALTER TABLE actividad_nuevo RENAME TO actividad;

CREATE INDEX ix_actividad_unidad ON actividad (unidad_id);
CREATE INDEX ix_actividad_contenido ON actividad (contenido_id);

CREATE UNIQUE INDEX ux_actividad_prueba_final_por_unidad
    ON actividad (unidad_id)
    WHERE tipo = 'PRUEBA_FINAL';

-- ---------------------------------------------------------------------
-- 3) Demo: actividad y desafio de cada unidad ligados a un contenido de esa
--    unidad; la prueba final queda transversal (NULL) porque evalua la
--    unidad completa.
-- ---------------------------------------------------------------------

UPDATE actividad SET contenido_id = 1 WHERE id = 1;   -- u1 ACTIVIDAD -> OA 1
UPDATE actividad SET contenido_id = 2 WHERE id = 2;   -- u1 DESAFIO   -> OA 2
UPDATE actividad SET contenido_id = 3 WHERE id = 4;   -- u2 ACTIVIDAD -> OA 3
UPDATE actividad SET contenido_id = 4 WHERE id = 5;   -- u2 DESAFIO   -> OA 4
UPDATE actividad SET contenido_id = 5 WHERE id = 7;   -- u3 ACTIVIDAD -> OA 5
UPDATE actividad SET contenido_id = 6 WHERE id = 8;   -- u3 DESAFIO   -> OA 6
UPDATE actividad SET contenido_id = 7 WHERE id = 10;  -- u4 ACTIVIDAD -> OA 7
UPDATE actividad SET contenido_id = 8 WHERE id = 11;  -- u4 DESAFIO   -> OA 8

-- ---------------------------------------------------------------------
-- 4) Restituir la validacion de FK para el resto de la ejecucion.
-- ---------------------------------------------------------------------

PRAGMA foreign_keys = ON;
