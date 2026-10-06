package com.example.mpaz_pro_6springboot.docencia.asignacion_actividad.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * La clave primaria es compuesta (asignacion_id, actividad_id), por eso no lleva campo id.
 */
public record AsignacionActividadCreateRequest(

        @NotNull
        Long asignacionId,

        @NotNull
        Long actividadId,

        /** Se repite porque la FK compuesta (actividad_id, unidad_id) la exige. */
        @NotNull
        Long unidadId
) {
}