-- Desacopla la disponibilidad de la prueba final del límite de intentos.
-- Antes: max_intentos NULL significaba "prueba final no habilitada" (decision 3).
-- Ahora: la disponibilidad se controla con prueba_final_habilitada y max_intentos
-- pasa a ser solo el límite (NULL = sin límite).

ALTER TABLE asignacion
    ADD COLUMN prueba_final_habilitada boolean NOT NULL DEFAULT false;

-- Las asignaciones del seed ya tenían max_intentos, es decir, estaban habilitadas.
UPDATE asignacion SET prueba_final_habilitada = true;
