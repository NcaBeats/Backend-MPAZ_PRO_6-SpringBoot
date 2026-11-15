package com.example.mpaz_pro_6springboot.informe.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/** DTO de creacion de DetalleInformePedagogico. Las referencias se envian como identificadores. */
public record DetalleInformePedagogicoRequest(
        @NotNull @Positive Long informePedagogicoId,
        @NotNull @Positive Long objetivoAprendizajeId,
        @NotNull @PositiveOrZero Integer correctas,
        @NotNull @PositiveOrZero Integer incorrectas
) {
}
