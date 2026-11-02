package com.example.mpaz_pro_6springboot.curriculo.dto.response;

/** DTO de respuesta de Contenido. */
public record ContenidoResponse(
        Long id,
        String descripcion,
        EstadoAutorizacionResumen estado,
        UnidadResumen unidad
) {
}
