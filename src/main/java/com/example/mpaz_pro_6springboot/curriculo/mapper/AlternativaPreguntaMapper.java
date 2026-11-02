package com.example.mpaz_pro_6springboot.curriculo.mapper;

import com.example.mpaz_pro_6springboot.common.mapper.EntityRefMapper;
import com.example.mpaz_pro_6springboot.common.mapper.ResumenMapper;
import com.example.mpaz_pro_6springboot.curriculo.dto.request.AlternativaPreguntaRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.AlternativaPreguntaResponse;
import com.example.mpaz_pro_6springboot.curriculo.model.AlternativaPregunta;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper MapStruct de AlternativaPregunta.
 *
 * <p>Las relaciones se mapean como referencias con solo el identificador;
 * el servicio las resuelve contra el repositorio antes de persistir.</p>
 */
@Mapper(
        componentModel = "spring",
        uses = {ResumenMapper.class, EntityRefMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AlternativaPreguntaMapper {

    AlternativaPreguntaResponse toResponse(AlternativaPregunta entity);

    List<AlternativaPreguntaResponse> toResponseList(List<AlternativaPregunta> entities);

    @Mapping(source = "preguntaId", target = "pregunta")
    @Mapping(source = "alternativaId", target = "alternativa")
    AlternativaPregunta toEntity(AlternativaPreguntaRequest request);
}
