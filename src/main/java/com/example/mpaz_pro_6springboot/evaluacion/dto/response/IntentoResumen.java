package com.example.mpaz_pro_6springboot.evaluacion.dto.response;

import java.time.Instant;

/** Resumen de Intento usado en respuestas anidadas. */
public record IntentoResumen(
        Long id,
        Instant fechaInicio,
        Instant fechaFin
) {
}
