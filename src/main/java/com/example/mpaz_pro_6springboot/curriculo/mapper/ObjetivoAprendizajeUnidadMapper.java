package com.example.mpaz_pro_6springboot.curriculo.mapper;

import com.example.mpaz_pro_6springboot.common.mapper.EntityRefMapper;
import com.example.mpaz_pro_6springboot.common.mapper.ResumenMapper;
import com.example.mpaz_pro_6springboot.curriculo.dto.request.ObjetivoAprendizajeUnidadRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.ObjetivoAprendizajeUnidadResponse;
import com.example.mpaz_pro_6springboot.curriculo.model.ObjetivoAprendizajeUnidad;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper MapStruct de ObjetivoAprendizajeUnidad.
 *
 * <p>Las relaciones se mapean como referencias con solo el identificador;
 * el servicio las resuelve contra el repositorio antes de persistir.</p>
 */
@Mapper(
        componentModel = "spring",
        uses = {ResumenMapper.class, EntityRefMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ObjetivoAprendizajeUnidadMapper {

    ObjetivoAprendizajeUnidadResponse toResponse(ObjetivoAprendizajeUnidad entity);

    List<ObjetivoAprendizajeUnidadResponse> toResponseList(List<ObjetivoAprendizajeUnidad> entities);

    @Mapping(source = "objetivoAprendizajeId", target = "objetivoAprendizaje")
    @Mapping(source = "unidadId", target = "unidad")
    ObjetivoAprendizajeUnidad toEntity(ObjetivoAprendizajeUnidadRequest request);
}
