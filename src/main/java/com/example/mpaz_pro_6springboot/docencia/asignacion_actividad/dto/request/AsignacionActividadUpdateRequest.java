package com.example.mpaz_pro_6springboot.docencia.asignacion_actividad.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * asignacion_id y actividad_id forman la clave, por eso no son actualizables.
 */
public record AsignacionActividadUpdateRequest(

        @NotNull
        Long unidadId
) {
}