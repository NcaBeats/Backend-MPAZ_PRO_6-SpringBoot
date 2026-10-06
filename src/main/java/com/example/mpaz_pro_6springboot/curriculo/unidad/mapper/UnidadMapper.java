package com.example.mpaz_pro_6springboot.curriculo.unidad;

import com.example.mpaz_pro_6springboot.curriculo.asignatura.mapper.AsignaturaMapper;
import com.example.mpaz_pro_6springboot.curriculo.unidad.dto.request.UnidadCreateRequest;
import com.example.mpaz_pro_6springboot.curriculo.unidad.dto.request.UnidadUpdateRequest;
import com.example.mpaz_pro_6springboot.curriculo.unidad.dto.response.UnidadResponse;
import com.example.mpaz_pro_6springboot.curriculo.unidad.model.Unidad;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {AsignaturaMapper.class})
public interface UnidadMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "asignatura", ignore = true)
    @Mapping(target = "autorizadaPor", ignore = true)
    @Mapping(target = "fechaAutorizacion", ignore = true)
    @Mapping(target = "objetivosAprendizaje", ignore = true)
    @Mapping(target = "actividades", ignore = true)
    @Mapping(target = "asignaciones", ignore = true)
    @Mapping(target = "informes", ignore = true)
    Unidad toEntity(UnidadCreateRequest request);

    void updateEntity(UnidadUpdateRequest request, @MappingTarget Unidad entity);

    UnidadResponse toResponse(Unidad entity);
}
