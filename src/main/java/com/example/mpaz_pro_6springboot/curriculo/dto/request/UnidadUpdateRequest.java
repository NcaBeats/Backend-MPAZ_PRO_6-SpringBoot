package com.example.mpaz_pro_6springboot.curriculo.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** DTO de actualizacion parcial de Unidad. Todos los campos son opcionales. */
public record UnidadUpdateRequest(
        @Size(max = 50) String nombre,
        String descripcion,
        @Positive Long asignaturaId,
        @Positive Long estadoId
) {
}
