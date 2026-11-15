package com.example.mpaz_pro_6springboot.informe.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Instant;

/**
 * Peticion para generar un informe pedagogico a partir de los resultados de
 * los ultimos intentos cerrados de una unidad.
 *
 * <p>{@code fechaCorte} debe ser el valor devuelto por el endpoint de
 * previsualizacion. Si se omite, el servicio usa el instante actual.</p>
 */
public record GenerarInformeRequest(
        @NotNull @Positive Long usuarioId,
        @NotNull @Positive Long unidadId,
        Instant fecha,
        String descripcion,
        Instant fechaCorte
) {
}
