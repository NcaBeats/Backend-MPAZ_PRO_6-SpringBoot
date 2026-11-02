package com.example.mpaz_pro_6springboot.curriculo.dto.response;

/** DTO de respuesta de Alternativa. */
public record AlternativaResponse(
        Long id,
        String enunciado,
        RecursoResumen recurso,
        boolean correcta
) {
}
