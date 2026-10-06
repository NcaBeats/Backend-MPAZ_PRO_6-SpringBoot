package com.example.mpaz_pro_6springboot.evaluacion.intento.dto.request;

import java.time.Instant;

/**
 * estudiante_id, actividad_id y numero son la clave logica del intento, no se actualizan.
 */
public record IntentoUpdateRequest(

        Instant fechaFin
) {
}