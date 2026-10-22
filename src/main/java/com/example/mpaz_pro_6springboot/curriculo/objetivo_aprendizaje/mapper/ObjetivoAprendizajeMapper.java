package com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.mapper;

import com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.dto.request.ObjetivoAprendizajeCreateRequest;
import com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.dto.request.ObjetivoAprendizajeUpdateRequest;
import com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.dto.response.ObjetivoAprendizajeResponse;
import com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.model.ObjetivoAprendizaje;
import com.example.mpaz_pro_6springboot.curriculo.unidad.mapper.UnidadMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = UnidadMapper.class)
public interface ObjetivoAprendizajeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "unidad", ignore = true)
    @Mapping(target = "contenidos", ignore = true)
    @Mapping(target = "preguntas", ignore = true)
    @Mapping(target = "asignacionesOa", ignore = true)
    @Mapping(target = "detallesInforme", ignore = true)
    ObjetivoAprendizaje toEntity(ObjetivoAprendizajeCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "unidad", ignore = true)
    @Mapping(target = "contenidos", ignore = true)
    @Mapping(target = "preguntas", ignore = true)
    @Mapping(target = "asignacionesOa", ignore = true)
    @Mapping(target = "detallesInforme", ignore = true)
    ObjetivoAprendizaje toEntity(ObjetivoAprendizajeUpdateRequest request);

    void updateEntity(ObjetivoAprendizajeUpdateRequest request, @MappingTarget ObjetivoAprendizaje entity);

    @Mapping(target = "unidadId", source = "unidad.id")
    ObjetivoAprendizajeResponse toResponse(ObjetivoAprendizaje entity);
}
