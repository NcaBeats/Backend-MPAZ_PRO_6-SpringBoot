package com.example.mpaz_pro_6springboot.docencia.docencia.dto.request;

import jakarta.validation.constraints.NotNull;

public record DocenciaUpdateRequest(
        @NotNull Long docenteId
) {
}
