package com.example.mpaz_pro_6springboot.curriculo.dto.response;

/** DTO de respuesta de AlternativaPregunta. */
public record AlternativaPreguntaResponse(
        Long id,
        PreguntaResumen pregunta,
        AlternativaResumen alternativa
) {
}
