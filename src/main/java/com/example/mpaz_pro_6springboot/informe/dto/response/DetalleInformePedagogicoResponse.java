package com.example.mpaz_pro_6springboot.informe.dto.response;

import com.example.mpaz_pro_6springboot.curriculo.dto.response.ObjetivoAprendizajeResumen;

/** DTO de respuesta de DetalleInformePedagogico. */
public record DetalleInformePedagogicoResponse(
        Long id,
        InformePedagogicoResumen informePedagogico,
        ObjetivoAprendizajeResumen objetivoAprendizaje,
        Integer correctas,
        Integer incorrectas
) {
}
