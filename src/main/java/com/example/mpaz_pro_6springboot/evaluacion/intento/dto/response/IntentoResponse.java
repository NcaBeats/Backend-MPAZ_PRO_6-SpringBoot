package com.example.mpaz_pro_6springboot.evaluacion.intento.dto.response;

import java.time.Instant;

public record IntentoResponse(
        Long id,
        Long estudianteId,
        Long actividadId,
        Integer numero,
        Instant fechaInicio,
        Instant fechaFin
) {
}