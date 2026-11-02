package com.example.mpaz_pro_6springboot.identidad.mapper;

import com.example.mpaz_pro_6springboot.common.mapper.EntityRefMapper;
import com.example.mpaz_pro_6springboot.common.mapper.ResumenMapper;
import com.example.mpaz_pro_6springboot.identidad.dto.request.SeccionRequest;
import com.example.mpaz_pro_6springboot.identidad.dto.request.SeccionUpdateRequest;
import com.example.mpaz_pro_6springboot.identidad.dto.response.SeccionResponse;
import com.example.mpaz_pro_6springboot.identidad.model.Seccion;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper MapStruct de Seccion.
 *
 * <p>Las relaciones se mapean como referencias con solo el identificador;
 * el servicio las resuelve contra el repositorio antes de persistir.</p>
 */
@Mapper(
        componentModel = "spring",
        uses = {ResumenMapper.class, EntityRefMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface SeccionMapper {

    SeccionResponse toResponse(Seccion entity);

    List<SeccionResponse> toResponseList(List<Seccion> entities);

    Seccion toEntity(SeccionRequest request);

    Seccion toEntity(SeccionUpdateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(SeccionUpdateRequest request, @MappingTarget Seccion entity);
}
