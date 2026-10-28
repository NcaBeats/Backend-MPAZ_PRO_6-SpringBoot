-- El contenido y sus recursos comparten el ciclo de vida del material:
-- BORRADOR, EN_REVISION, AUTORIZADO o PUBLICADO.
--
-- La migración deja el estado existente alineado con la unidad que contiene
-- el OA. A partir de este cambio, la unidad es la que gobierna la publicación
-- del material y sus contenidos no pueden quedar "invisibles" por un estado
-- desincronizado.

ALTER TABLE contenido
    ADD COLUMN estado varchar(20) NOT NULL DEFAULT 'BORRADOR';

ALTER TABLE contenido
    ADD CONSTRAINT ck_contenido_estado
        CHECK (estado IN ('BORRADOR', 'EN_REVISION', 'AUTORIZADO', 'PUBLICADO'));

ALTER TABLE recurso_contenido
    ADD COLUMN estado varchar(20) NOT NULL DEFAULT 'BORRADOR';

ALTER TABLE recurso_contenido
    ADD CONSTRAINT ck_recurso_estado
        CHECK (estado IN ('BORRADOR', 'EN_REVISION', 'AUTORIZADO', 'PUBLICADO'));

UPDATE contenido c
SET estado = u.estado
FROM unidad u
JOIN objetivo_aprendizaje oa ON oa.unidad_id = u.id
WHERE c.oa_id = oa.id;

UPDATE recurso_contenido r
SET estado = c.estado
FROM contenido c
WHERE r.contenido_id = c.id;

CREATE INDEX ix_contenido_estado ON contenido (estado);
CREATE INDEX ix_recurso_contenido_estado ON recurso_contenido (estado);
