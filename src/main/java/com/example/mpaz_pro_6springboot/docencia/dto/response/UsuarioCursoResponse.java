package com.example.mpaz_pro_6springboot.docencia.dto.response;

import com.example.mpaz_pro_6springboot.identidad.dto.response.CursoResumen;
import com.example.mpaz_pro_6springboot.identidad.dto.response.UsuarioResumen;

/** DTO de respuesta de UsuarioCurso. */
public record UsuarioCursoResponse(
        Long id,
        UsuarioResumen usuario,
        CursoResumen curso
) {
}
