package com.example.mpaz_pro_6springboot.curriculo.mapper;

import com.example.mpaz_pro_6springboot.common.mapper.EntityRefMapper;
import com.example.mpaz_pro_6springboot.common.mapper.ResumenMapper;
import com.example.mpaz_pro_6springboot.curriculo.dto.request.TipoActividadRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.request.TipoActividadUpdateRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.TipoActividadResponse;
import com.example.mpaz_pro_6springboot.curriculo.model.TipoActividad;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper MapStruct de TipoActividad.
 *
 * <p>Las relaciones se mapean como referencias con solo el identificador;
 * el servicio las resuelve contra el repositorio antes de persistir.</p>
 */
@Mapper(
        componentModel = "spring",
        uses = {ResumenMapper.class, EntityRefMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TipoActividadMapper {

    TipoActividadResponse toResponse(TipoActividad entity);

    List<TipoActividadResponse> toResponseList(List<TipoActividad> entities);

    TipoActividad toEntity(TipoActividadRequest request);

    TipoActividad toEntity(TipoActividadUpdateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(TipoActividadUpdateRequest request, @MappingTarget TipoActividad entity);
}
