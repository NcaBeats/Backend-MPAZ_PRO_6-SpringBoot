package com.example.mpaz_pro_6springboot.curriculo.recurso_contenido.dto.response;

import com.example.mpaz_pro_6springboot.common.enums.OrigenMaterial;
import com.example.mpaz_pro_6springboot.common.enums.TipoRecurso;

public record RecursoContenidoResponse(
        Long id,
        Long contenidoId,
        TipoRecurso tipo,
        String url,
        String descripcion,
        OrigenMaterial origen
) {
}