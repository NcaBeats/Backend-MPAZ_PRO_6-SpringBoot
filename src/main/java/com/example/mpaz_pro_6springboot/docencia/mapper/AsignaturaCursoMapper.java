package com.example.mpaz_pro_6springboot.docencia.mapper;

import com.example.mpaz_pro_6springboot.common.mapper.EntityRefMapper;
import com.example.mpaz_pro_6springboot.common.mapper.ResumenMapper;
import com.example.mpaz_pro_6springboot.docencia.dto.request.AsignaturaCursoRequest;
import com.example.mpaz_pro_6springboot.docencia.dto.response.AsignaturaCursoResponse;
import com.example.mpaz_pro_6springboot.docencia.model.AsignaturaCurso;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper MapStruct de AsignaturaCurso.
 *
 * <p>Las relaciones se mapean como referencias con solo el identificador;
 * el servicio las resuelve contra el repositorio antes de persistir.</p>
 */
@Mapper(
        componentModel = "spring",
        uses = {ResumenMapper.class, EntityRefMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AsignaturaCursoMapper {

    AsignaturaCursoResponse toResponse(AsignaturaCurso entity);

    List<AsignaturaCursoResponse> toResponseList(List<AsignaturaCurso> entities);

    @Mapping(source = "cursoId", target = "curso")
    @Mapping(source = "asignaturaId", target = "asignatura")
    @Mapping(source = "docenteId", target = "docente")
    AsignaturaCurso toEntity(AsignaturaCursoRequest request);
}
