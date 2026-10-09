package com.example.mpaz_pro_6springboot.docencia.asignacion.dto.request;

import jakarta.validation.constraints.Min;

import java.time.Instant;

public record AsignacionUpdateRequest(

        @Min(1)
        Integer maxIntentos,

        Boolean pruebaFinalHabilitada,

        Instant fecha
) {
}