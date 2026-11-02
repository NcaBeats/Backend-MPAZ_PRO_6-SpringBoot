package com.example.mpaz_pro_6springboot.identidad.dto.request;

import jakarta.validation.constraints.Positive;

/** DTO de actualizacion parcial de Curso. Todos los campos son opcionales. */
public record CursoUpdateRequest(
        @Positive Integer annio,
        @Positive Long nivelId,
        @Positive Long seccionId
) {
}
