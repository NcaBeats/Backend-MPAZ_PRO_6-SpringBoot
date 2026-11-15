package com.example.mpaz_pro_6springboot.docencia.dto.request;

import java.time.Instant;

import jakarta.validation.constraints.Positive;

/**
 * DTO de actualizacion parcial de ActividadCurso.
 *
 * <p>Solo cambia la configuracion de la asignacion (ventana y limite de
 * intentos). La pareja actividad-curso no se reasigna. Los flags
 * {@code habilitada} y {@code visible} se cambian con metodos explicitos del
 * servicio.</p>
 */
public record ActividadCursoUpdateRequest(
        Instant fechaInicio,
        Instant fechaFin,
        @Positive Integer maxIntentos
) {
}
