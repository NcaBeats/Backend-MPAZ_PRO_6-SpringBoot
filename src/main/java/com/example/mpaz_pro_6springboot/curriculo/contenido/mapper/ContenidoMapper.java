package com.example.mpaz_pro_6springboot.curriculo.contenido.mapper;

import com.example.mpaz_pro_6springboot.curriculo.contenido.dto.request.ContenidoCreateRequest;
import com.example.mpaz_pro_6springboot.curriculo.contenido.dto.request.ContenidoUpdateRequest;
import com.example.mpaz_pro_6springboot.curriculo.contenido.dto.response.ContenidoResponse;
import com.example.mpaz_pro_6springboot.curriculo.contenido.model.Contenido;
import com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.mapper.ObjetivoAprendizajeMapper;
import com.example.mpaz_pro_6springboot.curriculo.recurso_contenido.mapper.RecursoContenidoMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {ObjetivoAprendizajeMapper.class, RecursoContenidoMapper.class})
public interface ContenidoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "oa", ignore = true)
    @Mapping(target = "recursos", ignore = true)
    Contenido toEntity(ContenidoCreateRequest request);

    void updateEntity(ContenidoUpdateRequest request, @MappingTarget Contenido entity);

    @Mapping(target = "oaId", source = "oa.id")
    ContenidoResponse toResponse(Contenido entity);
}
