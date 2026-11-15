package com.example.mpaz_pro_6springboot.curriculo.dto.request;

import jakarta.validation.constraints.Size;

/** DTO de actualizacion parcial de TipoActividad. Todos los campos son opcionales. */
public record TipoActividadUpdateRequest(
        @Size(max = 50) String nombre
) {
}
