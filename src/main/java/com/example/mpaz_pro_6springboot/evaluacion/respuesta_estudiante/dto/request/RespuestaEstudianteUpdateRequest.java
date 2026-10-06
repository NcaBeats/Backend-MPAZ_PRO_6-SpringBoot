package com.example.mpaz_pro_6springboot.evaluacion.respuesta_estudiante.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * intento_id y pregunta_id forman el unico (intento_id, pregunta_id), no se actualizan.
 */
public record RespuestaEstudianteUpdateRequest(

        Long alternativaId,

        @NotNull
        Boolean esCorrecta
) {
}