package com.example.mpaz_pro_6springboot.curriculo.mapper;

import com.example.mpaz_pro_6springboot.common.mapper.EntityRefMapper;
import com.example.mpaz_pro_6springboot.common.mapper.ResumenMapper;
import com.example.mpaz_pro_6springboot.curriculo.dto.request.ObjetivoAprendizajeRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.request.ObjetivoAprendizajeUpdateRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.ObjetivoAprendizajeResponse;
import com.example.mpaz_pro_6springboot.curriculo.model.ObjetivoAprendizaje;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper MapStruct de ObjetivoAprendizaje.
 *
 * <p>Las relaciones se mapean como referencias con solo el identificador;
 * el servicio las resuelve contra el repositorio antes de persistir.</p>
 */
@Mapper(
        componentModel = "spring",
        uses = {ResumenMapper.class, EntityRefMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ObjetivoAprendizajeMapper {

    ObjetivoAprendizajeResponse toResponse(ObjetivoAprendizaje entity);

    List<ObjetivoAprendizajeResponse> toResponseList(List<ObjetivoAprendizaje> entities);

    ObjetivoAprendizaje toEntity(ObjetivoAprendizajeRequest request);

    ObjetivoAprendizaje toEntity(ObjetivoAprendizajeUpdateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(ObjetivoAprendizajeUpdateRequest request, @MappingTarget ObjetivoAprendizaje entity);
}
