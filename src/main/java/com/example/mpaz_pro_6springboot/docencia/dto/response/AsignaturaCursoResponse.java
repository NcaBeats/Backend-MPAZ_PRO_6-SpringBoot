package com.example.mpaz_pro_6springboot.docencia.dto.response;

import com.example.mpaz_pro_6springboot.curriculo.dto.response.AsignaturaResumen;
import com.example.mpaz_pro_6springboot.identidad.dto.response.CursoResumen;

/** DTO de respuesta de AsignaturaCurso. */
public record AsignaturaCursoResponse(
        Long id,
        CursoResumen curso,
        AsignaturaResumen asignatura
) {
}
