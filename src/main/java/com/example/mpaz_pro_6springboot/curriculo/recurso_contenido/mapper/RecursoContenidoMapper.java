package com.example.mpaz_pro_6springboot.curriculo.recurso_contenido.mapper;

import com.example.mpaz_pro_6springboot.curriculo.recurso_contenido.dto.request.RecursoContenidoCreateRequest;
import com.example.mpaz_pro_6springboot.curriculo.recurso_contenido.dto.request.RecursoContenidoUpdateRequest;
import com.example.mpaz_pro_6springboot.curriculo.recurso_contenido.dto.response.RecursoContenidoResponse;
import com.example.mpaz_pro_6springboot.curriculo.recurso_contenido.model.RecursoContenido;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RecursoContenidoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contenido", ignore = true)
    RecursoContenido toEntity(RecursoContenidoCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contenido", ignore = true)
    RecursoContenido toEntity(RecursoContenidoUpdateRequest request);

    void updateEntity(RecursoContenidoUpdateRequest request, @MappingTarget RecursoContenido entity);

    @Mapping(target = "contenidoId", source = "contenido.id")
    RecursoContenidoResponse toResponse(RecursoContenido entity);
}
