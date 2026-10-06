package com.example.mpaz_pro_6springboot.docencia.asignacion_actividad.mapper;

import com.example.mpaz_pro_6springboot.docencia.asignacion_actividad.dto.request.AsignacionActividadCreateRequest;
import com.example.mpaz_pro_6springboot.docencia.asignacion_actividad.dto.request.AsignacionActividadUpdateRequest;
import com.example.mpaz_pro_6springboot.docencia.asignacion_actividad.dto.response.AsignacionActividadResponse;
import com.example.mpaz_pro_6springboot.docencia.asignacion_actividad.model.AsignacionActividad;
import com.example.mpaz_pro_6springboot.curriculo.actividad.mapper.ActividadMapper;
import com.example.mpaz_pro_6springboot.docencia.asignacion.mapper.AsignacionMapper;
import com.example.mpaz_pro_6springboot.curriculo.unidad.mapper.UnidadMapper;
import com.example.mpaz_pro_6springboot.docencia.asignacion_actividad.model.AsignacionActividadId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {AsignacionMapper.class, ActividadMapper.class, UnidadMapper.class})
public interface AsignacionActividadMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "asignacion", ignore = true)
    @Mapping(target = "actividad", ignore = true)
    AsignacionActividad toEntity(AsignacionActividadCreateRequest request);

    void updateEntity(AsignacionActividadUpdateRequest request, @MappingTarget AsignacionActividad entity);

    AsignacionActividadResponse toResponse(AsignacionActividad entity);

    @Mapping(target = "asignacionId", source = "id.asignacionId")
    @Mapping(target = "actividadId", source = "id.actividadId")
    AsignacionActividadResponse toResponse(AsignacionActividad entity);
}
