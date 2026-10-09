package com.example.mpaz_pro_6springboot.docencia.asignacion.dto.response;

import java.time.Instant;

public record AsignacionResponse(
        Long id,
        Long cursoId,
        Long unidadId,
        Long docenteId,
        Integer maxIntentos,
        Boolean pruebaFinalHabilitada,
        Instant fecha
) {
}