package com.example.mpaz_pro_6springboot.curriculo.mapper;

import com.example.mpaz_pro_6springboot.common.mapper.EntityRefMapper;
import com.example.mpaz_pro_6springboot.common.mapper.ResumenMapper;
import com.example.mpaz_pro_6springboot.curriculo.dto.request.PreguntaActividadRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.PreguntaActividadResponse;
import com.example.mpaz_pro_6springboot.curriculo.model.PreguntaActividad;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper MapStruct de PreguntaActividad.
 *
 * <p>Las relaciones se mapean como referencias con solo el identificador;
 * el servicio las resuelve contra el repositorio antes de persistir.</p>
 */
@Mapper(
        componentModel = "spring",
        uses = {ResumenMapper.class, EntityRefMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PreguntaActividadMapper {

    PreguntaActividadResponse toResponse(PreguntaActividad entity);

    List<PreguntaActividadResponse> toResponseList(List<PreguntaActividad> entities);

    @Mapping(source = "actividadId", target = "actividad")
    @Mapping(source = "preguntaId", target = "pregunta")
    PreguntaActividad toEntity(PreguntaActividadRequest request);
}
