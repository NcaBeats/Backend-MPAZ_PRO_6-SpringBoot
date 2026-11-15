package com.example.mpaz_pro_6springboot.curriculo.mapper;

import com.example.mpaz_pro_6springboot.common.mapper.EntityRefMapper;
import com.example.mpaz_pro_6springboot.common.mapper.ResumenMapper;
import com.example.mpaz_pro_6springboot.curriculo.dto.request.ObjetivoAprendizajeContenidoRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.ObjetivoAprendizajeContenidoResponse;
import com.example.mpaz_pro_6springboot.curriculo.model.ObjetivoAprendizajeContenido;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper MapStruct de ObjetivoAprendizajeContenido.
 *
 * <p>Las relaciones se mapean como referencias con solo el identificador;
 * el servicio las resuelve contra el repositorio antes de persistir.</p>
 */
@Mapper(
        componentModel = "spring",
        uses = {ResumenMapper.class, EntityRefMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ObjetivoAprendizajeContenidoMapper {

    ObjetivoAprendizajeContenidoResponse toResponse(ObjetivoAprendizajeContenido entity);

    List<ObjetivoAprendizajeContenidoResponse> toResponseList(List<ObjetivoAprendizajeContenido> entities);

    @Mapping(source = "objetivoAprendizajeId", target = "objetivoAprendizaje")
    @Mapping(source = "contenidoId", target = "contenido")
    ObjetivoAprendizajeContenido toEntity(ObjetivoAprendizajeContenidoRequest request);
}
