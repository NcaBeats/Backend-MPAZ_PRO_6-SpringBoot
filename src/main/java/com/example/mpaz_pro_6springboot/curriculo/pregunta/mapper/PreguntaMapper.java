package com.example.mpaz_pro_6springboot.curriculo.pregunta.mapper;

import com.example.mpaz_pro_6springboot.curriculo.pregunta.dto.request.PreguntaCreateRequest;
import com.example.mpaz_pro_6springboot.curriculo.pregunta.dto.request.PreguntaUpdateRequest;
import com.example.mpaz_pro_6springboot.curriculo.pregunta.dto.response.PreguntaResponse;
import com.example.mpaz_pro_6springboot.curriculo.pregunta.model.Pregunta;
import com.example.mpaz_pro_6springboot.curriculo.actividad.mapper.ActividadMapper;
import com.example.mpaz_pro_6springboot.curriculo.alternativa.mapper.AlternativaMapper;
import com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.mapper.ObjetivoAprendizajeMapper;
import com.example.mpaz_pro_6springboot.curriculo.unidad.mapper.UnidadMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {ActividadMapper.class, UnidadMapper.class, ObjetivoAprendizajeMapper.class, AlternativaMapper.class})
public interface PreguntaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "actividad", ignore = true)
    @Mapping(target = "unidad", ignore = true)
    @Mapping(target = "oa", ignore = true)
    @Mapping(target = "alternativas", ignore = true)
    @Mapping(target = "respuestasEstudiante", ignore = true)
    Pregunta toEntity(PreguntaCreateRequest request);

    void updateEntity(PreguntaUpdateRequest request, @MappingTarget Pregunta entity);

    @Mapping(target = "actividadId", source = "actividad.id")
    @Mapping(target = "unidadId", source = "unidad.id")
    @Mapping(target = "oaId", source = "oa.id")
    PreguntaResponse toResponse(Pregunta entity);
}
