package com.example.mpaz_pro_6springboot.curriculo.dto.response;

/** Resumen de Alternativa usado en respuestas anidadas. */
public record AlternativaResumen(
        Long id,
        String enunciado,
        boolean correcta
) {
}
