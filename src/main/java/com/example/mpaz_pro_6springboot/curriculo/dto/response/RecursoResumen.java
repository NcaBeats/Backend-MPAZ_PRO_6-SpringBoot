package com.example.mpaz_pro_6springboot.curriculo.dto.response;

/** Resumen de Recurso usado en respuestas anidadas. */
public record RecursoResumen(
        Long id,
        String nombre,
        TipoRecursoResumen tipoRecurso
) {
}
