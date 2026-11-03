package com.example.mpaz_pro_6springboot.curriculo.dto.request;

import jakarta.validation.constraints.Positive;

/** DTO de actualizacion parcial de Actividad. Todos los campos son opcionales. */
public record ActividadUpdateRequest(
        String instrucciones,
        @Positive Long tipoActividadId,
        @Positive Long contenidoId,
        @Positive Long estadoId
) {
}
