-- =====================================================================
-- Relacion opcional actividad -> contenido
--
-- Una actividad puede pertenecer a un contenido de su misma unidad. Si
-- contenido_id es nulo, la actividad es transversal a la unidad (por ejemplo,
-- la prueba final). La pertenencia a la misma unidad se valida en el Service.
-- =====================================================================

ALTER TABLE actividad
    ADD COLUMN contenido_id bigint;

ALTER TABLE actividad
    ADD CONSTRAINT fk_actividad_contenido
        FOREIGN KEY (contenido_id) REFERENCES contenido (id);

CREATE INDEX index_actividad_contenido_id
    ON actividad (contenido_id);
