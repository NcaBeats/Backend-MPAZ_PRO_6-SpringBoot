package com.example.mpaz_pro_6springboot.evaluacion.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Instant;

/** DTO de creacion de Intento. Las referencias se envian como identificadores. */
public record IntentoRequest(
        @NotNull @Positive Long usuarioId,
        @NotNull @Positive Long actividadId,
        Instant fechaInicio,
        Instant fechaFin
) {
}
