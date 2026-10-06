package com.example.mpaz_pro_6springboot.docencia.asignacion_oa.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * asignacion_id y oa_id forman la clave, por eso no son actualizables.
 */
public record AsignacionOaUpdateRequest(

        @NotNull
        Long unidadId
) {
}