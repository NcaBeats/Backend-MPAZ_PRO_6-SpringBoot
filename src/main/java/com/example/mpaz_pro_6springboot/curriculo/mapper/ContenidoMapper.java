package com.example.mpaz_pro_6springboot.curriculo.mapper;

import com.example.mpaz_pro_6springboot.common.mapper.EntityRefMapper;
import com.example.mpaz_pro_6springboot.common.mapper.ResumenMapper;
import com.example.mpaz_pro_6springboot.curriculo.dto.request.ContenidoRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.request.ContenidoUpdateRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.ContenidoResponse;
import com.example.mpaz_pro_6springboot.curriculo.model.Contenido;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper MapStruct de Contenido.
 *
 * <p>Las relaciones se mapean como referencias con solo el identificador;
 * el servicio las resuelve contra el repositorio antes de persistir.</p>
 */
@Mapper(
        componentModel = "spring",
        uses = {ResumenMapper.class, EntityRefMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ContenidoMapper {

    ContenidoResponse toResponse(Contenido entity);

    List<ContenidoResponse> toResponseList(List<Contenido> entities);

    @Mapping(source = "estadoId", target = "estado")
    @Mapping(source = "unidadId", target = "unidad")
    Contenido toEntity(ContenidoRequest request);

    @Mapping(source = "estadoId", target = "estado")
    @Mapping(source = "unidadId", target = "unidad")
    Contenido toEntity(ContenidoUpdateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "unidad", ignore = true)
    void updateEntity(ContenidoUpdateRequest request, @MappingTarget Contenido entity);
}
