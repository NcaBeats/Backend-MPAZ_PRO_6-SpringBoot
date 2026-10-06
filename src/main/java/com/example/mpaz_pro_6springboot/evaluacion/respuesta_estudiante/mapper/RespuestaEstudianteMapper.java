package com.example.mpaz_pro_6springboot.evaluacion.respuesta_estudiante.mapper;

import com.example.mpaz_pro_6springboot.evaluacion.respuesta_estudiante.dto.request.RespuestaEstudianteCreateRequest;
import com.example.mpaz_pro_6springboot.evaluacion.respuesta_estudiante.dto.request.RespuestaEstudianteUpdateRequest;
import com.example.mpaz_pro_6springboot.evaluacion.respuesta_estudiante.dto.response.RespuestaEstudianteResponse;
import com.example.mpaz_pro_6springboot.evaluacion.respuesta_estudiante.model.RespuestaEstudiante;
import com.example.mpaz_pro_6springboot.curriculo.alternativa.mapper.AlternativaMapper;
import com.example.mpaz_pro_6springboot.evaluacion.intento.mapper.IntentoMapper;
import com.example.mpaz_pro_6springboot.curriculo.pregunta.mapper.PreguntaMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {IntentoMapper.class, PreguntaMapper.class, AlternativaMapper.class})
public interface RespuestaEstudianteMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "intento", ignore = true)
    @Mapping(target = "pregunta", ignore = true)
    @Mapping(target = "alternativa", ignore = true)
    RespuestaEstudiante toEntity(RespuestaEstudianteCreateRequest request);

    void updateEntity(RespuestaEstudianteUpdateRequest request, @MappingTarget RespuestaEstudiante entity);

    RespuestaEstudianteResponse toResponse(RespuestaEstudiante entity);
}
