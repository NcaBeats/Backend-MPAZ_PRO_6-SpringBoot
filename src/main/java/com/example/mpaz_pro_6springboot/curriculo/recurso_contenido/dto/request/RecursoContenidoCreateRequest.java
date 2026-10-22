package com.example.mpaz_pro_6springboot.curriculo.recurso_contenido.dto.request;

import com.example.mpaz_pro_6springboot.common.enums.OrigenMaterial;
import com.example.mpaz_pro_6springboot.common.enums.TipoRecurso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RecursoContenidoCreateRequest(

        @NotNull
        Long contenidoId,

        @NotNull
        TipoRecurso tipo,

        @NotBlank
        @Size(max = 500)
        String url,

        @Size(max = 500)
        String descripcion,

        @NotNull
        OrigenMaterial origen
) {
}