package com.example.mpaz_pro_6springboot.identidad.dto.request;

import jakarta.validation.constraints.Size;

/** DTO de actualizacion parcial de Seccion. Todos los campos son opcionales. */
public record SeccionUpdateRequest(
        @Size(max = 1) String letra
) {
}
