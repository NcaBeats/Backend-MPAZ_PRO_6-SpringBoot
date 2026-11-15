package com.example.mpaz_pro_6springboot.evaluacion.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** DTO de creacion de Respuesta. Las referencias se envian como identificadores. */
public record RespuestaRequest(
        @NotNull @Positive Long intentoId,
        @NotNull @Positive Long preguntaId,
        @NotNull @Positive Long alternativaId
) {
}
