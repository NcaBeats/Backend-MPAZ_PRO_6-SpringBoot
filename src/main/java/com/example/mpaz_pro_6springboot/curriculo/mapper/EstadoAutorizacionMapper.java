package com.example.mpaz_pro_6springboot.curriculo.mapper;

import com.example.mpaz_pro_6springboot.common.mapper.EntityRefMapper;
import com.example.mpaz_pro_6springboot.common.mapper.ResumenMapper;
import com.example.mpaz_pro_6springboot.curriculo.dto.request.EstadoAutorizacionRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.request.EstadoAutorizacionUpdateRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.EstadoAutorizacionResponse;
import com.example.mpaz_pro_6springboot.curriculo.model.EstadoAutorizacion;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper MapStruct de EstadoAutorizacion.
 *
 * <p>Las relaciones se mapean como referencias con solo el identificador;
 * el servicio las resuelve contra el repositorio antes de persistir.</p>
 */
@Mapper(
        componentModel = "spring",
        uses = {ResumenMapper.class, EntityRefMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface EstadoAutorizacionMapper {

    EstadoAutorizacionResponse toResponse(EstadoAutorizacion entity);

    List<EstadoAutorizacionResponse> toResponseList(List<EstadoAutorizacion> entities);

    EstadoAutorizacion toEntity(EstadoAutorizacionRequest request);

    EstadoAutorizacion toEntity(EstadoAutorizacionUpdateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(EstadoAutorizacionUpdateRequest request, @MappingTarget EstadoAutorizacion entity);
}
