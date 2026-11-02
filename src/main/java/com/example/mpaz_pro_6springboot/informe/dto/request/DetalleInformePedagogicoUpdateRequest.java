package com.example.mpaz_pro_6springboot.informe.dto.request;

import jakarta.validation.constraints.PositiveOrZero;

/** DTO de actualizacion parcial de DetalleInformePedagogico. Todos los campos son opcionales. */
public record DetalleInformePedagogicoUpdateRequest(
        @PositiveOrZero Integer correctas,
        @PositiveOrZero Integer incorrectas
) {
}
