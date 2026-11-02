package com.example.mpaz_pro_6springboot.evaluacion.dto.response;

import com.example.mpaz_pro_6springboot.curriculo.dto.response.ActividadResumen;
import com.example.mpaz_pro_6springboot.identidad.dto.response.UsuarioResumen;
import java.time.Instant;

/** DTO de respuesta de Intento. */
public record IntentoResponse(
        Long id,
        Instant fechaInicio,
        Instant fechaFin,
        UsuarioResumen usuario,
        ActividadResumen actividad
) {
}
