package com.example.mpaz_pro_6springboot.curriculo.contenido.dto.response;

import com.example.mpaz_pro_6springboot.common.enums.OrigenMaterial;

public record ContenidoResponse(
        Long id,
        Long oaId,
        String titulo,
        String explicacion,
        String ejemplos,
        String instrucciones,
        Integer orden,
        OrigenMaterial origen
) {
}