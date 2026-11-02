package com.example.mpaz_pro_6springboot.curriculo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** DTO de creacion de Unidad. Las referencias se envian como identificadores. */
public record UnidadRequest(
        @NotBlank @Size(max = 50) String nombre,
        String descripcion,
        @NotNull @Positive Long asignaturaId,
        @NotNull @Positive Long estadoId
) {
}
