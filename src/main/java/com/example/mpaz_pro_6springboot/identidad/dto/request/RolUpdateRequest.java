package com.example.mpaz_pro_6springboot.identidad.dto.request;

import jakarta.validation.constraints.Size;

/** DTO de actualizacion parcial de Rol. Todos los campos son opcionales. */
public record RolUpdateRequest(
        @Size(max = 25) String nombre
) {
}
