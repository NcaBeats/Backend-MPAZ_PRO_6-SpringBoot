package com.example.mpaz_pro_6springboot.curriculo.dto.response;

/** DTO de respuesta de Recurso. */
public record RecursoResponse(
        Long id,
        String nombre,
        String url,
        String descripcion,
        TipoRecursoResumen tipoRecurso,
        ContenidoResumen contenido
) {
}
