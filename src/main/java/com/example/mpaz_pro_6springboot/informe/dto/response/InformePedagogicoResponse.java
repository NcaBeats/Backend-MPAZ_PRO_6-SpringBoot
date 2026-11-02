package com.example.mpaz_pro_6springboot.informe.dto.response;

import com.example.mpaz_pro_6springboot.curriculo.dto.response.UnidadResumen;
import com.example.mpaz_pro_6springboot.identidad.dto.response.UsuarioResumen;
import java.time.Instant;

/** DTO de respuesta de InformePedagogico. */
public record InformePedagogicoResponse(
        Long id,
        UsuarioResumen usuario,
        Instant fecha,
        String descripcion,
        UnidadResumen unidad
) {
}
