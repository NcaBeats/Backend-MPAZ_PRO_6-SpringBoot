package com.example.mpaz_pro_6springboot.curriculo.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** DTO de actualizacion parcial de Recurso. Todos los campos son opcionales. */
public record RecursoUpdateRequest(
        @Size(max = 100) String nombre,
        @Size(max = 255) String url,
        @Size(max = 255) String descripcion,
        @Positive Long tipoRecursoId,
        @Positive Long contenidoId
) {
}
