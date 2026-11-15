package com.example.mpaz_pro_6springboot.identidad.mapper;

import com.example.mpaz_pro_6springboot.common.mapper.EntityRefMapper;
import com.example.mpaz_pro_6springboot.common.mapper.ResumenMapper;
import com.example.mpaz_pro_6springboot.identidad.dto.request.RolRequest;
import com.example.mpaz_pro_6springboot.identidad.dto.request.RolUpdateRequest;
import com.example.mpaz_pro_6springboot.identidad.dto.response.RolResponse;
import com.example.mpaz_pro_6springboot.identidad.model.Rol;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper MapStruct de Rol.
 *
 * <p>Las relaciones se mapean como referencias con solo el identificador;
 * el servicio las resuelve contra el repositorio antes de persistir.</p>
 */
@Mapper(
        componentModel = "spring",
        uses = {ResumenMapper.class, EntityRefMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface RolMapper {

    RolResponse toResponse(Rol entity);

    List<RolResponse> toResponseList(List<Rol> entities);

    Rol toEntity(RolRequest request);

    Rol toEntity(RolUpdateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(RolUpdateRequest request, @MappingTarget Rol entity);
}
