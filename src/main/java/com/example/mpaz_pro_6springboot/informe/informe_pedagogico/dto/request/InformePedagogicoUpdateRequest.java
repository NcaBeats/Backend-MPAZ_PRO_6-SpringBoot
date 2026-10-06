package com.example.mpaz_pro_6springboot.informe.informe_pedagogico.dto.request;

import com.example.mpaz_pro_6springboot.common.enums.EstadoInforme;

import java.time.Instant;

public record InformePedagogicoUpdateRequest(

        EstadoInforme estado,

        Instant fechaValidacion
) {
}