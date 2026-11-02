package com.example.mpaz_pro_6springboot.docencia.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** DTO de creacion de UsuarioCurso. Las referencias se envian como identificadores. */
public record UsuarioCursoRequest(
        @NotNull @Positive Long usuarioId,
        @NotNull @Positive Long cursoId
) {
}
