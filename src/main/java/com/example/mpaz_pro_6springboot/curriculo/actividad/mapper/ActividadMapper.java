package com.example.mpaz_pro_6springboot.curriculo.actividad;

import com.example.mpaz_pro_6springboot.curriculo.actividad.dto.request.ActividadCreateRequest;
import com.example.mpaz_pro_6springboot.curriculo.actividad.dto.request.ActividadUpdateRequest;
import com.example.mpaz_pro_6springboot.curriculo.actividad.dto.response.ActividadResponse;
import com.example.mpaz_pro_6springboot.curriculo.actividad.model.Actividad;
import com.example.mpaz_pro_6springboot.curriculo.unidad.mapper.UnidadMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = UnidadMapper.class)
public interface ActividadMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "unidad", ignore = true)
    @Mapping(target = "preguntas", ignore = true)
    @Mapping(target = "asignacionesActividad", ignore = true)
    @Mapping(target = "intentos", ignore = true)
    Actividad toEntity(ActividadCreateRequest request);

    void updateEntity(ActividadUpdateRequest request, @MappingTarget Actividad entity);

    ActividadResponse toResponse(Actividad entity);
}
