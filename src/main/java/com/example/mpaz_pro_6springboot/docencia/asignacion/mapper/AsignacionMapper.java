package com.example.mpaz_pro_6springboot.docencia.asignacion.mapper;

import com.example.mpaz_pro_6springboot.docencia.asignacion.dto.request.AsignacionCreateRequest;
import com.example.mpaz_pro_6springboot.docencia.asignacion.dto.request.AsignacionUpdateRequest;
import com.example.mpaz_pro_6springboot.docencia.asignacion.dto.response.AsignacionResponse;
import com.example.mpaz_pro_6springboot.docencia.asignacion.model.Asignacion;
import com.example.mpaz_pro_6springboot.identidad.curso.mapper.CursoMapper;
import com.example.mpaz_pro_6springboot.curriculo.unidad.mapper.UnidadMapper;
import com.example.mpaz_pro_6springboot.identidad.usuario.mapper.UsuarioMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {CursoMapper.class, UnidadMapper.class, UsuarioMapper.class})
public interface AsignacionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "curso", ignore = true)
    @Mapping(target = "unidad", ignore = true)
    @Mapping(target = "docente", ignore = true)
    @Mapping(target = "asignacionesOa", ignore = true)
    @Mapping(target = "asignacionesActividad", ignore = true)
    Asignacion toEntity(AsignacionCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "curso", ignore = true)
    @Mapping(target = "unidad", ignore = true)
    @Mapping(target = "docente", ignore = true)
    @Mapping(target = "asignacionesOa", ignore = true)
    @Mapping(target = "asignacionesActividad", ignore = true)
    Asignacion toEntity(AsignacionUpdateRequest request);

    void updateEntity(AsignacionUpdateRequest request, @MappingTarget Asignacion entity);

    @Mapping(target = "cursoId", source = "curso.id")
    @Mapping(target = "unidadId", source = "unidad.id")
    @Mapping(target = "docenteId", source = "docente.id")
    AsignacionResponse toResponse(Asignacion entity);
}
