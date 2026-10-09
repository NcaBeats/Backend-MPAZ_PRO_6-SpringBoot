package com.example.mpaz_pro_6springboot.docencia.asignacion.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record AsignacionCreateRequest(

        @NotNull
        Long cursoId,

        @NotNull
        Long unidadId,

        @NotNull
        Long docenteId,

        /** CHECK max_intentos > 0. Si se omite el Service aplica 1. */
        @Min(1)
        Integer maxIntentos,

        /** Habilita la prueba final de la unidad. Si se omite, queda deshabilitada. */
        boolean pruebaFinalHabilitada,

        @NotNull
        Instant fecha
) {
}