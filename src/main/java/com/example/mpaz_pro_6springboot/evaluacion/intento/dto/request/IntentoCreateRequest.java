package com.example.mpaz_pro_6springboot.evaluacion.intento.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record IntentoCreateRequest(

        @NotNull
        Long estudianteId,

        @NotNull
        Long actividadId,

        /** CHECK numero > 0. El Service calcula el siguiente correlativo si se omite. */
        @Min(1)
        Integer numero,

        @NotNull
        Instant fechaInicio,

        Instant fechaFin
) {
}