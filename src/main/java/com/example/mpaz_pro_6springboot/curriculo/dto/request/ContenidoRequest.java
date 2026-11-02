package com.example.mpaz_pro_6springboot.curriculo.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** DTO de creacion de Contenido. Las referencias se envian como identificadores. */
public record ContenidoRequest(
        String descripcion,
        @NotNull @Positive Long estadoId,
        @NotNull @Positive Long unidadId
) {
}
