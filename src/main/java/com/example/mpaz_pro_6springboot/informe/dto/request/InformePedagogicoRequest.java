package com.example.mpaz_pro_6springboot.informe.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Instant;

/** DTO de creacion de InformePedagogico. Las referencias se envian como identificadores. */
public record InformePedagogicoRequest(
        @NotNull @Positive Long usuarioId,
        @NotNull @Positive Long unidadId,
        Instant fecha,
        String descripcion
) {
}
