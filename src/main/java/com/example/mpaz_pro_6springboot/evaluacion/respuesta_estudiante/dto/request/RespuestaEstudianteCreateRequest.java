package com.example.mpaz_pro_6springboot.evaluacion.respuesta_estudiante.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * El intento se toma de la ruta ({intentoId}); el Service deriva {@code esCorrecta}
 * a partir de la alternativa seleccionada para congelar el resultado.
 */
public record RespuestaEstudianteCreateRequest(

        @NotNull
        Long preguntaId,

        @NotNull
        Long alternativaId
) {
}
