package com.example.mpaz_pro_6springboot.curriculo.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** DTO de creacion de AlternativaPregunta. Las referencias se envian como identificadores. */
public record AlternativaPreguntaRequest(
        @NotNull @Positive Long preguntaId,
        @NotNull @Positive Long alternativaId
) {
}
