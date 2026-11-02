package com.example.mpaz_pro_6springboot.evaluacion.mapper;

import com.example.mpaz_pro_6springboot.common.mapper.EntityRefMapper;
import com.example.mpaz_pro_6springboot.common.mapper.ResumenMapper;
import com.example.mpaz_pro_6springboot.evaluacion.dto.request.IntentoRequest;
import com.example.mpaz_pro_6springboot.evaluacion.dto.response.IntentoResponse;
import com.example.mpaz_pro_6springboot.evaluacion.model.Intento;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper MapStruct de Intento.
 *
 * <p>Las relaciones se mapean como referencias con solo el identificador;
 * el servicio las resuelve contra el repositorio antes de persistir.</p>
 */
@Mapper(
        componentModel = "spring",
        uses = {ResumenMapper.class, EntityRefMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface IntentoMapper {

    IntentoResponse toResponse(Intento entity);

    List<IntentoResponse> toResponseList(List<Intento> entities);

    @Mapping(source = "usuarioId", target = "usuario")
    @Mapping(source = "actividadId", target = "actividad")
    Intento toEntity(IntentoRequest request);
}
