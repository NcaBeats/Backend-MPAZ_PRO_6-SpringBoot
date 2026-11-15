package com.example.mpaz_pro_6springboot.informe.dto.request;

import java.time.Instant;

/** DTO de actualizacion parcial de InformePedagogico. Todos los campos son opcionales. */
public record InformePedagogicoUpdateRequest(
        Instant fecha,
        String descripcion
) {
}
