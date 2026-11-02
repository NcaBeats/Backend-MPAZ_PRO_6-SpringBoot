package com.example.mpaz_pro_6springboot.evaluacion.mapper;

import com.example.mpaz_pro_6springboot.common.mapper.EntityRefMapper;
import com.example.mpaz_pro_6springboot.common.mapper.ResumenMapper;
import com.example.mpaz_pro_6springboot.evaluacion.dto.request.RespuestaRequest;
import com.example.mpaz_pro_6springboot.evaluacion.dto.response.RespuestaResponse;
import com.example.mpaz_pro_6springboot.evaluacion.model.Respuesta;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper MapStruct de Respuesta.
 *
 * <p>Las relaciones se mapean como referencias con solo el identificador;
 * el servicio las resuelve contra el repositorio antes de persistir.</p>
 */
@Mapper(
        componentModel = "spring",
        uses = {ResumenMapper.class, EntityRefMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface RespuestaMapper {

    RespuestaResponse toResponse(Respuesta entity);

    List<RespuestaResponse> toResponseList(List<Respuesta> entities);

    @Mapping(source = "intentoId", target = "intento")
    @Mapping(source = "preguntaId", target = "pregunta")
    @Mapping(source = "alternativaId", target = "alternativa")
    Respuesta toEntity(RespuestaRequest request);
}
