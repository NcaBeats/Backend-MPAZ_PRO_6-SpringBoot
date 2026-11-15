-- Asigna opcionalmente un docente responsable a cada asignatura impartida
-- en un curso. Las asignaciones existentes quedan sin docente asignado.

ALTER TABLE asignatura_curso
    ADD COLUMN docente_id bigint;

ALTER TABLE asignatura_curso
    ADD CONSTRAINT asignatura_curso_docente_fk
    FOREIGN KEY (docente_id) REFERENCES usuario (usuario_id);

CREATE INDEX idx_asignatura_curso_docente
    ON asignatura_curso (docente_id);
