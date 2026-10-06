package com.example.mpaz_pro_6springboot.identidad.usuario.mapper;

import com.example.mpaz_pro_6springboot.identidad.curso.mapper.CursoMapper;
import com.example.mpaz_pro_6springboot.identidad.usuario.dto.request.UsuarioCreateRequest;
import com.example.mpaz_pro_6springboot.identidad.usuario.dto.request.UsuarioUpdateRequest;
import com.example.mpaz_pro_6springboot.identidad.usuario.dto.response.UsuarioResponse;
import com.example.mpaz_pro_6springboot.identidad.usuario.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = CursoMapper.class)
public interface UsuarioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "curso", ignore = true)
    @Mapping(target = "intentos", ignore = true)
    @Mapping(target = "respuestasEstudiante", ignore = true)
    @Mapping(target = "informesEstudiante", ignore = true)
    @Mapping(target = "informesDocente", ignore = true)
    @Mapping(target = "unidadesAutorizadas", ignore = true)
    @Mapping(target = "asignacionesDocente", ignore = true)
    Usuario toEntity(UsuarioCreateRequest request);

    void updateEntity(UsuarioUpdateRequest request, @MappingTarget Usuario entity);

    @Mapping(target = "cursoId", source = "curso.id")
    UsuarioResponse toResponse(Usuario entity);
}
