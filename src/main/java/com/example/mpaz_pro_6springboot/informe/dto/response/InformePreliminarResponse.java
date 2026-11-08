package com.example.mpaz_pro_6springboot.informe.dto.response;

import com.example.mpaz_pro_6springboot.curriculo.dto.response.UnidadResumen;
import com.example.mpaz_pro_6springboot.identidad.dto.response.UsuarioResumen;
import java.time.Instant;
import java.util.List;

/**
 * Consolidado efimero del desempeno de un estudiante en una unidad.
 *
 * <p>No se persiste: sirve para revisar el resultado antes de crear el
 * informe pedagogico. El {@code fechaCorte} devuelto debe enviarse al
 * endpoint de creacion para que el snapshot sea consistente.</p>
 */
public record InformePreliminarResponse(
        UsuarioResumen usuario,
        UnidadResumen unidad,
        Instant fechaCorte,
        List<ActividadEvaluadaResumen> actividades,
        List<DetallePreliminarResponse> detalles,
        int totalCorrectas,
        int totalIncorrectas
) {
}
