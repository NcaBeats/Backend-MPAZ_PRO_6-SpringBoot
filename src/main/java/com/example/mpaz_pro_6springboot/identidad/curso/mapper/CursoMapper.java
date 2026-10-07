package com.example.mpaz_pro_6springboot.identidad.curso.mapper;

import com.example.mpaz_pro_6springboot.identidad.curso.dto.request.CursoCreateRequest;
import com.example.mpaz_pro_6springboot.identidad.curso.dto.request.CursoUpdateRequest;
import com.example.mpaz_pro_6springboot.identidad.curso.dto.response.CursoResponse;
import com.example.mpaz_pro_6springboot.identidad.curso.model.Curso;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CursoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuarios", ignore = true)
    @Mapping(target = "asignaciones", ignore = true)
    Curso toEntity(CursoCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuarios", ignore = true)
    @Mapping(target = "asignaciones", ignore = true)
    Curso toEntity(CursoUpdateRequest request);

    void updateEntity(CursoUpdateRequest request, @MappingTarget Curso entity);

    CursoResponse toResponse(Curso entity);
}
