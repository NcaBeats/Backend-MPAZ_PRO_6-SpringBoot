package com.example.mpaz_pro_6springboot.curriculo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** DTO de creacion de Pregunta. Las referencias se envian como identificadores. */
public record PreguntaRequest(
        @NotBlank String enunciado,
        @NotNull @Positive Long recursoId
) {
}
