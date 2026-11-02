package com.example.mpaz_pro_6springboot.curriculo.dto.response;

/** DTO de respuesta de ObjetivoAprendizajeUnidad. */
public record ObjetivoAprendizajeUnidadResponse(
        Long id,
        ObjetivoAprendizajeResumen objetivoAprendizaje,
        UnidadResumen unidad
) {
}
