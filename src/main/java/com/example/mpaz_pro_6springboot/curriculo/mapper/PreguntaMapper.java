package com.example.mpaz_pro_6springboot.curriculo.mapper;

import com.example.mpaz_pro_6springboot.common.mapper.EntityRefMapper;
import com.example.mpaz_pro_6springboot.common.mapper.ResumenMapper;
import com.example.mpaz_pro_6springboot.curriculo.dto.request.PreguntaRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.request.PreguntaUpdateRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.PreguntaResponse;
import com.example.mpaz_pro_6springboot.curriculo.model.Pregunta;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper MapStruct de Pregunta.
 *
 * <p>Las relaciones se mapean como referencias con solo el identificador;
 * el servicio las resuelve contra el repositorio antes de persistir.</p>
 */
@Mapper(
        componentModel = "spring",
        uses = {ResumenMapper.class, EntityRefMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PreguntaMapper {

    PreguntaResponse toResponse(Pregunta entity);

    List<PreguntaResponse> toResponseList(List<Pregunta> entities);

    @Mapping(source = "recursoId", target = "recurso")
    Pregunta toEntity(PreguntaRequest request);

    @Mapping(source = "recursoId", target = "recurso")
    Pregunta toEntity(PreguntaUpdateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "recurso", ignore = true)
    void updateEntity(PreguntaUpdateRequest request, @MappingTarget Pregunta entity);
}
