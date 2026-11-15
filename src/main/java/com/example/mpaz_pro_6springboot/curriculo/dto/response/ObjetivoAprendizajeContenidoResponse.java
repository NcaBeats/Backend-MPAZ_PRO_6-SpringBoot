package com.example.mpaz_pro_6springboot.curriculo.dto.response;

/** DTO de respuesta de ObjetivoAprendizajeContenido. */
public record ObjetivoAprendizajeContenidoResponse(
        Long id,
        ObjetivoAprendizajeResumen objetivoAprendizaje,
        ContenidoResumen contenido,
        UnidadResumen unidad
) {
}
