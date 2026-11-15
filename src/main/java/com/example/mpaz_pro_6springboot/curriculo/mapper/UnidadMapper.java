package com.example.mpaz_pro_6springboot.curriculo.mapper;

import com.example.mpaz_pro_6springboot.common.mapper.EntityRefMapper;
import com.example.mpaz_pro_6springboot.common.mapper.ResumenMapper;
import com.example.mpaz_pro_6springboot.curriculo.dto.request.UnidadRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.request.UnidadUpdateRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.UnidadResponse;
import com.example.mpaz_pro_6springboot.curriculo.model.Unidad;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper MapStruct de Unidad.
 *
 * <p>Las relaciones se mapean como referencias con solo el identificador;
 * el servicio las resuelve contra el repositorio antes de persistir.</p>
 */
@Mapper(
        componentModel = "spring",
        uses = {ResumenMapper.class, EntityRefMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UnidadMapper {

    UnidadResponse toResponse(Unidad entity);

    List<UnidadResponse> toResponseList(List<Unidad> entities);

    @Mapping(source = "asignaturaId", target = "asignatura")
    @Mapping(source = "estadoId", target = "estado")
    Unidad toEntity(UnidadRequest request);

    @Mapping(source = "asignaturaId", target = "asignatura")
    @Mapping(source = "estadoId", target = "estado")
    Unidad toEntity(UnidadUpdateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "asignatura", ignore = true)
    @Mapping(target = "estado", ignore = true)
    void updateEntity(UnidadUpdateRequest request, @MappingTarget Unidad entity);
}
