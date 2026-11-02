package com.example.mpaz_pro_6springboot.curriculo.dto.request;

import jakarta.validation.constraints.Size;

/** DTO de actualizacion parcial de Asignatura. Todos los campos son opcionales. */
public record AsignaturaUpdateRequest(
        @Size(max = 50) String nombre
) {
}
