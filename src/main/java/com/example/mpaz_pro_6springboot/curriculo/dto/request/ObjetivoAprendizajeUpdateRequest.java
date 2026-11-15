package com.example.mpaz_pro_6springboot.curriculo.dto.request;

import jakarta.validation.constraints.Size;

/** DTO de actualizacion parcial de ObjetivoAprendizaje. Todos los campos son opcionales. */
public record ObjetivoAprendizajeUpdateRequest(
        String descripcion,
        @Size(max = 25) String codigo
) {
}
