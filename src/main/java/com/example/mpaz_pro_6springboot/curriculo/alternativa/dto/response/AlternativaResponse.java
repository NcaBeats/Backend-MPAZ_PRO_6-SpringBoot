package com.example.mpaz_pro_6springboot.curriculo.alternativa.dto.response;

public record AlternativaResponse(
        Long id,
        Long preguntaId,
        String texto,
        Boolean esCorrecta
) {
}