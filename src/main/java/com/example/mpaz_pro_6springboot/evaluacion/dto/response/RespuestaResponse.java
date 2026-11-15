package com.example.mpaz_pro_6springboot.evaluacion.dto.response;

import com.example.mpaz_pro_6springboot.curriculo.dto.response.AlternativaResumen;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.PreguntaResumen;

/** DTO de respuesta de Respuesta. */
public record RespuestaResponse(
        Long id,
        IntentoResumen intento,
        PreguntaResumen pregunta,
        AlternativaResumen alternativa
) {
}
