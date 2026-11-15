package com.example.mpaz_pro_6springboot.curriculo.dto.request;

import jakarta.validation.constraints.Positive;

/** DTO de actualizacion parcial de Contenido. Todos los campos son opcionales. */
public record ContenidoUpdateRequest(
        String descripcion,
        @Positive Long estadoId,
        @Positive Long unidadId
) {
}
