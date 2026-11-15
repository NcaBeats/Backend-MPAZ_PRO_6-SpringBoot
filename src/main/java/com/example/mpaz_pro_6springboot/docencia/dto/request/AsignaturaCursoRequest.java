package com.example.mpaz_pro_6springboot.docencia.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** DTO de creacion de AsignaturaCurso. Las referencias se envian como identificadores. */
public record AsignaturaCursoRequest(
        @NotNull @Positive Long cursoId,
        @NotNull @Positive Long asignaturaId,
        @Positive Long docenteId
) {
}
