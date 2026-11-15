-- ============================================================
-- MPAZ PRO 6 - V5: corte temporal y origen del informe pedagogico
--
-- Objetivo:
--   1. El informe pedagogico se emite como una fotografia (snapshot) del
--      desempeno del estudiante en una unidad. Los conteos correctas e
--      incorrectas viven en detalle_informe_pedagogico y no se recalculan.
--   2. fecha_corte registra el instante maximo de los intentos incluidos.
--      Asi el informe sigue siendo reproducible aunque el estudiante
--      realice intentos despues de creado.
--   3. origen distingue los informes generados desde resultados
--      (AUTOMATICO) de los creados o corregidos manualmente (MANUAL).
--
-- V1 a V4 ya estan aplicadas: no se editan. Esta es la migracion incremental.
-- ============================================================

ALTER TABLE informe_pedagogico
    ADD COLUMN fecha_corte timestamptz;

-- Los informes existentes no tienen un corte tecnico registrado; se usa su
-- fecha de emision como aproximacion.
UPDATE informe_pedagogico
SET fecha_corte = fecha
WHERE fecha_corte IS NULL;

-- Un informe nuevo que no indique corte (creacion manual) toma el instante
-- de insercion. El flujo automatico siempre envia un corte explicito.
ALTER TABLE informe_pedagogico
    ALTER COLUMN fecha_corte SET DEFAULT now();

ALTER TABLE informe_pedagogico
    ALTER COLUMN fecha_corte SET NOT NULL;

ALTER TABLE informe_pedagogico
    ADD COLUMN origen varchar(20) NOT NULL DEFAULT 'MANUAL';

ALTER TABLE informe_pedagogico
    ADD CONSTRAINT informe_pedagogico_origen_ck
    CHECK (origen IN ('AUTOMATICO', 'MANUAL'));

CREATE INDEX idx_informe_pedagogico_usuario_unidad_corte
    ON informe_pedagogico (usuario_id, unidad_id, fecha_corte DESC);

COMMENT ON COLUMN informe_pedagogico.fecha_corte IS
    'Instante maximo de los intentos incluidos en el informe; los intentos posteriores no alteran el informe.';

COMMENT ON COLUMN informe_pedagogico.origen IS
    'AUTOMATICO: generado desde resultados; MANUAL: creado o corregido manualmente.';
