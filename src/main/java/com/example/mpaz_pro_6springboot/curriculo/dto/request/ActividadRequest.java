package com.example.mpaz_pro_6springboot.curriculo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** DTO de creacion de Actividad. Las referencias se envian como identificadores. */
public record ActividadRequest(
        @NotBlank String instrucciones,
        @NotNull @Positive Long tipoActividadId,
        @NotNull @Positive Long contenidoId,
        @NotNull @Positive Long estadoId
) {
}
