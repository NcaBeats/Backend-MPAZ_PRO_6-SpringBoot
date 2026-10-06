package com.example.mpaz_pro_6springboot.identidad.curso.mapper;

import com.example.mpaz_pro_6springboot.identidad.curso.dto.request.CursoCreateRequest;
import com.example.mpaz_pro_6springboot.identidad.curso.dto.request.CursoUpdateRequest;
import com.example.mpaz_pro_6springboot.identidad.curso.dto.response.CursoResponse;
import com.example.mpaz_pro_6springboot.identidad.curso.model.Curso;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CursoMapper {

    CursoMapper INSTANCE = Mappers.getMapper(CursoMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuarios", ignore = true)
    @Mapping(target = "asignaciones", ignore = true)
    Curso toEntity(CursoCreateRequest request);

    void updateEntity(CursoUpdateRequest request, @MappingTarget Curso entity);

    CursoResponse toResponse(Curso entity);
}
