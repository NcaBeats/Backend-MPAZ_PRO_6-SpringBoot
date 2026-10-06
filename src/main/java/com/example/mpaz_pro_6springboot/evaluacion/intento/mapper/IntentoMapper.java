package com.example.mpaz_pro_6springboot.evaluacion.intento.mapper;

import com.example.mpaz_pro_6springboot.evaluacion.intento.dto.request.IntentoCreateRequest;
import com.example.mpaz_pro_6springboot.evaluacion.intento.dto.request.IntentoUpdateRequest;
import com.example.mpaz_pro_6springboot.evaluacion.intento.dto.response.IntentoResponse;
import com.example.mpaz_pro_6springboot.evaluacion.intento.model.Intento;
import com.example.mpaz_pro_6springboot.curriculo.actividad.mapper.ActividadMapper;
import com.example.mpaz_pro_6springboot.identidad.usuario.mapper.UsuarioMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {UsuarioMapper.class, ActividadMapper.class})
public interface IntentoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estudiante", ignore = true)
    @Mapping(target = "actividad", ignore = true)
    @Mapping(target = "respuestasEstudiante", ignore = true)
    Intento toEntity(IntentoCreateRequest request);

    void updateEntity(IntentoUpdateRequest request, @MappingTarget Intento entity);

    IntentoResponse toResponse(Intento entity);
}
