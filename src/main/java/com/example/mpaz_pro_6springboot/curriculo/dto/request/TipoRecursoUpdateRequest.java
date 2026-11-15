package com.example.mpaz_pro_6springboot.curriculo.dto.request;

import jakarta.validation.constraints.Size;

/** DTO de actualizacion parcial de TipoRecurso. Todos los campos son opcionales. */
public record TipoRecursoUpdateRequest(
        @Size(max = 25) String nombre
) {
}
