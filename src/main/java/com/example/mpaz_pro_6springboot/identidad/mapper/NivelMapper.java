package com.example.mpaz_pro_6springboot.identidad.mapper;

import com.example.mpaz_pro_6springboot.common.mapper.EntityRefMapper;
import com.example.mpaz_pro_6springboot.common.mapper.ResumenMapper;
import com.example.mpaz_pro_6springboot.identidad.dto.request.NivelRequest;
import com.example.mpaz_pro_6springboot.identidad.dto.request.NivelUpdateRequest;
import com.example.mpaz_pro_6springboot.identidad.dto.response.NivelResponse;
import com.example.mpaz_pro_6springboot.identidad.model.Nivel;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper MapStruct de Nivel.
 *
 * <p>Las relaciones se mapean como referencias con solo el identificador;
 * el servicio las resuelve contra el repositorio antes de persistir.</p>
 */
@Mapper(
        componentModel = "spring",
        uses = {ResumenMapper.class, EntityRefMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface NivelMapper {

    NivelResponse toResponse(Nivel entity);

    List<NivelResponse> toResponseList(List<Nivel> entities);

    Nivel toEntity(NivelRequest request);

    Nivel toEntity(NivelUpdateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(NivelUpdateRequest request, @MappingTarget Nivel entity);
}
