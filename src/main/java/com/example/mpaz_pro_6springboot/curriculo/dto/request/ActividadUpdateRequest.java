package com.example.mpaz_pro_6springboot.curriculo.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.Instant;

/** DTO de actualizacion parcial de Actividad. Todos los campos son opcionales. */
public record ActividadUpdateRequest(
        String instrucciones,
        @Positive Long tipoActividadId,
        @Positive Long contenidoId,
        Instant fechaInicio,
        Instant fechaTermino,
        @PositiveOrZero Integer maxIntentos,
        @Positive Long estadoId
) {
}
