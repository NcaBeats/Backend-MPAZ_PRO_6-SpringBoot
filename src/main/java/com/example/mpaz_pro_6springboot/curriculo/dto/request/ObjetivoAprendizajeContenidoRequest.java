package com.example.mpaz_pro_6springboot.curriculo.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** DTO de creacion de ObjetivoAprendizajeContenido. Las referencias se envian como identificadores. */
public record ObjetivoAprendizajeContenidoRequest(
        @NotNull @Positive Long objetivoAprendizajeId,
        @NotNull @Positive Long contenidoId
) {
}
