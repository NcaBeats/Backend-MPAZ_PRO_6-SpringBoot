package com.example.mpaz_pro_6springboot.curriculo.dto.response;

/** Resumen de Actividad usado en respuestas anidadas. */
public record ActividadResumen(
        Long id,
        String instrucciones,
        boolean habilitada
) {
}
