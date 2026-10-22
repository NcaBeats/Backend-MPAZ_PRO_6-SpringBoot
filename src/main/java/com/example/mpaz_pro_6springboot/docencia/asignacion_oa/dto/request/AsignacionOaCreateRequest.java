package com.example.mpaz_pro_6springboot.docencia.asignacion_oa.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * La clave primaria es compuesta (asignacion_id, oa_id), por eso no lleva campo id.
 */
public record AsignacionOaCreateRequest(

        @NotNull
        Long asignacionId,

        @NotNull
        Long oaId,

        /** Se repite porque las FK compuestas contra asignacion y objetivo_aprendizaje la exigen. */
        @NotNull
        Long unidadId
) {
}