package com.example.mpaz_pro_6springboot.docencia.asignacion_oa;

import com.example.mpaz_pro_6springboot.docencia.asignacion_oa.dto.request.AsignacionOaCreateRequest;
import com.example.mpaz_pro_6springboot.docencia.asignacion_oa.dto.request.AsignacionOaUpdateRequest;
import com.example.mpaz_pro_6springboot.docencia.asignacion_oa.dto.response.AsignacionOaResponse;
import com.example.mpaz_pro_6springboot.docencia.asignacion_oa.model.AsignacionOa;
import com.example.mpaz_pro_6springboot.docencia.asignacion.mapper.AsignacionMapper;
import com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.mapper.ObjetivoAprendizajeMapper;
import com.example.mpaz_pro_6springboot.curriculo.unidad.mapper.UnidadMapper;
import com.example.mpaz_pro_6springboot.docencia.asignacion_oa.model.AsignacionOaId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {AsignacionMapper.class, ObjetivoAprendizajeMapper.class, UnidadMapper.class})
public interface AsignacionOaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "asignacion", ignore = true)
    @Mapping(target = "objetivoAprendizaje", ignore = true)
    AsignacionOa toEntity(AsignacionOaCreateRequest request);

    void updateEntity(AsignacionOaUpdateRequest request, @MappingTarget AsignacionOa entity);

    AsignacionOaResponse toResponse(AsignacionOa entity);

    @Mapping(target = "asignacionId", source = "id.asignacionId")
    @Mapping(target = "oaId", source = "id.oaId")
    AsignacionOaResponse toResponse(AsignacionOa entity);
}
