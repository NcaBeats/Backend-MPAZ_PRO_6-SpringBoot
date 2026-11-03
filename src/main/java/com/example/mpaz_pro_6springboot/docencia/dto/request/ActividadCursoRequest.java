package com.example.mpaz_pro_6springboot.docencia.dto.request;

import java.time.Instant;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** DTO de creacion de ActividadCurso. Las referencias se envian como identificadores. */
public record ActividadCursoRequest(
        @NotNull @Positive Long actividadId,
        @NotNull @Positive Long cursoId,
        Instant fechaInicio,
        Instant fechaFin,
        boolean habilitada,
        boolean visible,
        @Positive Integer maxIntentos
) {
}
