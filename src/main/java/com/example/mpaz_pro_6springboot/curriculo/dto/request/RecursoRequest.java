package com.example.mpaz_pro_6springboot.curriculo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** DTO de creacion de Recurso. Las referencias se envian como identificadores. */
public record RecursoRequest(
        @NotBlank @Size(max = 100) String nombre,
        @Size(max = 255) String url,
        @Size(max = 255) String descripcion,
        @NotNull @Positive Long tipoRecursoId,
        @NotNull @Positive Long contenidoId
) {
}
