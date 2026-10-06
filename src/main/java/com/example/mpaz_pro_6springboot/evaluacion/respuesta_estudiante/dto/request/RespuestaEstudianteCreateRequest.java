package com.example.mpaz_pro_6springboot.evaluacion.respuesta_estudiante.dto.request;

import jakarta.validation.constraints.NotNull;

public record RespuestaEstudianteCreateRequest(

        @NotNull
        Long intentoId,

        @NotNull
        Long preguntaId,

        @NotNull
        Long alternativaId,

        @NotNull
        Boolean esCorrecta
) {
}