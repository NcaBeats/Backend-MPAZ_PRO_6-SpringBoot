package com.example.mpaz_pro_6springboot.docencia.docencia.dto.response;

public record DocenciaResponse(
        Long id,
        Long docenteId,
        String docenteNombre,
        Long cursoId,
        String cursoNombre,
        Long asignaturaId,
        String asignaturaNombre
) {
}
