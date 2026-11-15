package com.example.mpaz_pro_6springboot.curriculo.mapper;

import com.example.mpaz_pro_6springboot.common.mapper.EntityRefMapper;
import com.example.mpaz_pro_6springboot.common.mapper.ResumenMapper;
import com.example.mpaz_pro_6springboot.curriculo.dto.request.ActividadRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.request.ActividadUpdateRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.ActividadResponse;
import com.example.mpaz_pro_6springboot.curriculo.model.Actividad;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper MapStruct de Actividad.
 *
 * <p>Las relaciones se mapean como referencias con solo el identificador;
 * el servicio las resuelve contra el repositorio antes de persistir.</p>
 */
@Mapper(
        componentModel = "spring",
        uses = {ResumenMapper.class, EntityRefMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ActividadMapper {

    ActividadResponse toResponse(Actividad entity);

    List<ActividadResponse> toResponseList(List<Actividad> entities);

    @Mapping(source = "tipoActividadId", target = "tipoActividad")
    @Mapping(source = "contenidoId", target = "contenido")
    @Mapping(source = "estadoId", target = "estado")
    Actividad toEntity(ActividadRequest request);

    @Mapping(source = "tipoActividadId", target = "tipoActividad")
    @Mapping(source = "contenidoId", target = "contenido")
    @Mapping(source = "estadoId", target = "estado")
    Actividad toEntity(ActividadUpdateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "tipoActividad", ignore = true)
    @Mapping(target = "contenido", ignore = true)
    @Mapping(target = "estado", ignore = true)
    void updateEntity(ActividadUpdateRequest request, @MappingTarget Actividad entity);
}
