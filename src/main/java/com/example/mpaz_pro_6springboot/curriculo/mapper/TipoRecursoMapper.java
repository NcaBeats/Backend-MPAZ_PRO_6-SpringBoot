package com.example.mpaz_pro_6springboot.curriculo.mapper;

import com.example.mpaz_pro_6springboot.common.mapper.EntityRefMapper;
import com.example.mpaz_pro_6springboot.common.mapper.ResumenMapper;
import com.example.mpaz_pro_6springboot.curriculo.dto.request.TipoRecursoRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.request.TipoRecursoUpdateRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.TipoRecursoResponse;
import com.example.mpaz_pro_6springboot.curriculo.model.TipoRecurso;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper MapStruct de TipoRecurso.
 *
 * <p>Las relaciones se mapean como referencias con solo el identificador;
 * el servicio las resuelve contra el repositorio antes de persistir.</p>
 */
@Mapper(
        componentModel = "spring",
        uses = {ResumenMapper.class, EntityRefMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TipoRecursoMapper {

    TipoRecursoResponse toResponse(TipoRecurso entity);

    List<TipoRecursoResponse> toResponseList(List<TipoRecurso> entities);

    TipoRecurso toEntity(TipoRecursoRequest request);

    TipoRecurso toEntity(TipoRecursoUpdateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(TipoRecursoUpdateRequest request, @MappingTarget TipoRecurso entity);
}
