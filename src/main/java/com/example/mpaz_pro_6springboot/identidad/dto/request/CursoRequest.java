package com.example.mpaz_pro_6springboot.identidad.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** DTO de creacion de Curso. Las referencias se envian como identificadores. */
public record CursoRequest(
        @NotNull @Positive Integer annio,
        @NotNull @Positive Long nivelId,
        @NotNull @Positive Long seccionId
) {
}
