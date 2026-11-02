package com.example.mpaz_pro_6springboot.curriculo.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** DTO de creacion de PreguntaActividad. Las referencias se envian como identificadores. */
public record PreguntaActividadRequest(
        @NotNull @Positive Long actividadId,
        @NotNull @Positive Long preguntaId
) {
}
