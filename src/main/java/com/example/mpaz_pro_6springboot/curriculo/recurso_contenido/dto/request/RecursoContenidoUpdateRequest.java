package com.example.mpaz_pro_6springboot.curriculo.recurso_contenido.dto.request;

import com.example.mpaz_pro_6springboot.common.enums.OrigenMaterial;
import com.example.mpaz_pro_6springboot.common.enums.TipoRecurso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecursoContenidoUpdateRequest(

        TipoRecurso tipo,

        @NotBlank
        @Size(max = 500)
        String url,

        @Size(max = 500)
        String descripcion,

        OrigenMaterial origen
) {
}