package com.example.mpaz_pro_6springboot.identidad.mapper;

import com.example.mpaz_pro_6springboot.common.mapper.EntityRefMapper;
import com.example.mpaz_pro_6springboot.common.mapper.ResumenMapper;
import com.example.mpaz_pro_6springboot.identidad.dto.request.CursoRequest;
import com.example.mpaz_pro_6springboot.identidad.dto.request.CursoUpdateRequest;
import com.example.mpaz_pro_6springboot.identidad.dto.response.CursoResponse;
import com.example.mpaz_pro_6springboot.identidad.model.Curso;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper MapStruct de Curso.
 *
 * <p>Las relaciones se mapean como referencias con solo el identificador;
 * el servicio las resuelve contra el repositorio antes de persistir.</p>
 */
@Mapper(
        componentModel = "spring",
        uses = {ResumenMapper.class, EntityRefMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CursoMapper {

    CursoResponse toResponse(Curso entity);

    List<CursoResponse> toResponseList(List<Curso> entities);

    @Mapping(source = "nivelId", target = "nivel")
    @Mapping(source = "seccionId", target = "seccion")
    Curso toEntity(CursoRequest request);

    @Mapping(source = "nivelId", target = "nivel")
    @Mapping(source = "seccionId", target = "seccion")
    Curso toEntity(CursoUpdateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "nivel", ignore = true)
    @Mapping(target = "seccion", ignore = true)
    void updateEntity(CursoUpdateRequest request, @MappingTarget Curso entity);
}
