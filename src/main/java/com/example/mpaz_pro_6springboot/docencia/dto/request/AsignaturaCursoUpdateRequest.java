package com.example.mpaz_pro_6springboot.docencia.dto.request;

import jakarta.validation.constraints.Positive;

/**
 * Actualizacion de la relacion asignatura-curso.
 *
 * <p>{@code docenteId} nulo (o ausente) desasigna al docente actual.</p>
 */
public record AsignaturaCursoUpdateRequest(
        @Positive Long docenteId
) {
}
