package com.example.mpaz_pro_6springboot.informe.dto.response;

import java.util.List;

/** Informe pedagogico recien generado, con su cabecera y sus detalles. */
public record InformePedagogicoDetalladoResponse(
        InformePedagogicoResponse informe,
        List<DetalleInformePedagogicoResponse> detalles
) {
}
