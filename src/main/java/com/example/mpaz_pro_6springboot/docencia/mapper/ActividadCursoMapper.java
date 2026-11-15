package com.example.mpaz_pro_6springboot.docencia.mapper;

import com.example.mpaz_pro_6springboot.common.mapper.EntityRefMapper;
import com.example.mpaz_pro_6springboot.common.mapper.ResumenMapper;
import com.example.mpaz_pro_6springboot.docencia.dto.request.ActividadCursoRequest;
import com.example.mpaz_pro_6springboot.docencia.dto.request.ActividadCursoUpdateRequest;
import com.example.mpaz_pro_6springboot.docencia.dto.response.ActividadCursoResponse;
import com.example.mpaz_pro_6springboot.docencia.model.ActividadCurso;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper MapStruct de ActividadCurso.
 *
 * <p>Las relaciones se mapean como referencias con solo el identificador;
 * el servicio las resuelve contra el repositorio antes de persistir.</p>
 */
@Mapper(
        componentModel = "spring",
        uses = {ResumenMapper.class, EntityRefMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ActividadCursoMapper {

    ActividadCursoResponse toResponse(ActividadCurso entity);

    List<ActividadCursoResponse> toResponseList(List<ActividadCurso> entities);

    @Mapping(source = "actividadId", target = "actividad")
    @Mapping(source = "cursoId", target = "curso")
    ActividadCurso toEntity(ActividadCursoRequest request);

    ActividadCurso toEntity(ActividadCursoUpdateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "actividad", ignore = true)
    @Mapping(target = "curso", ignore = true)
    void updateEntity(ActividadCursoUpdateRequest request, @MappingTarget ActividadCurso entity);
}
