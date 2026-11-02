package com.example.mpaz_pro_6springboot.informe.dto.response;

import java.time.Instant;

/** Resumen de InformePedagogico usado en respuestas anidadas. */
public record InformePedagogicoResumen(
        Long id,
        Instant fecha
) {
}
