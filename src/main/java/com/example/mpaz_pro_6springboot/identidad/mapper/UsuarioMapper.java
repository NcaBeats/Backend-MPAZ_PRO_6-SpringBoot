package com.example.mpaz_pro_6springboot.identidad.mapper;

import com.example.mpaz_pro_6springboot.common.mapper.EntityRefMapper;
import com.example.mpaz_pro_6springboot.common.mapper.ResumenMapper;
import com.example.mpaz_pro_6springboot.identidad.dto.request.UsuarioRequest;
import com.example.mpaz_pro_6springboot.identidad.dto.request.UsuarioUpdateRequest;
import com.example.mpaz_pro_6springboot.identidad.dto.response.UsuarioResponse;
import com.example.mpaz_pro_6springboot.identidad.model.Usuario;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper MapStruct de Usuario.
 *
 * <p>Las relaciones se mapean como referencias con solo el identificador;
 * el servicio las resuelve contra el repositorio antes de persistir.</p>
 */
@Mapper(
        componentModel = "spring",
        uses = {ResumenMapper.class, EntityRefMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UsuarioMapper {

    UsuarioResponse toResponse(Usuario entity);

    List<UsuarioResponse> toResponseList(List<Usuario> entities);

    @Mapping(source = "rolId", target = "rol")
    @Mapping(target = "passwordHash", ignore = true)
    Usuario toEntity(UsuarioRequest request);

    @Mapping(source = "rolId", target = "rol")
    @Mapping(target = "passwordHash", ignore = true)
    Usuario toEntity(UsuarioUpdateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "rol", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    void updateEntity(UsuarioUpdateRequest request, @MappingTarget Usuario entity);
}
