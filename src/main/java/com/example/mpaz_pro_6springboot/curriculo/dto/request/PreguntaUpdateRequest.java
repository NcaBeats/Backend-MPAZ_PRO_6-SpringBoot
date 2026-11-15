package com.example.mpaz_pro_6springboot.curriculo.dto.request;

import jakarta.validation.constraints.Positive;

/** DTO de actualizacion parcial de Pregunta. Todos los campos son opcionales. */
public record PreguntaUpdateRequest(
        String enunciado,
        @Positive Long recursoId
) {
}
