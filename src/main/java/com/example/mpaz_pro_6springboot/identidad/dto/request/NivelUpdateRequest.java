package com.example.mpaz_pro_6springboot.identidad.dto.request;

import jakarta.validation.constraints.Size;

/** DTO de actualizacion parcial de Nivel. Todos los campos son opcionales. */
public record NivelUpdateRequest(
        @Size(max = 25) String nombre
) {
}
