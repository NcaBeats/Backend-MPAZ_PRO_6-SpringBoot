package com.example.mpaz_pro_6springboot.docencia.docencia.mapper;

import com.example.mpaz_pro_6springboot.docencia.docencia.dto.response.DocenciaResponse;
import com.example.mpaz_pro_6springboot.docencia.docencia.model.DocenteCursoAsignatura;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DocenciaMapper {

    @Mapping(target = "docenteId", source = "docente.id")
    @Mapping(target = "docenteNombre", source = "docente.nombre")
    @Mapping(target = "cursoId", source = "curso.id")
    @Mapping(target = "cursoNombre", source = "curso.nombre")
    @Mapping(target = "asignaturaId", source = "asignatura.id")
    @Mapping(target = "asignaturaNombre", source = "asignatura.nombre")
    DocenciaResponse toResponse(DocenteCursoAsignatura entity);
}
