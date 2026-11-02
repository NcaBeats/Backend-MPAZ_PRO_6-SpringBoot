package com.example.mpaz_pro_6springboot.curriculo.mapper;

import com.example.mpaz_pro_6springboot.common.mapper.EntityRefMapper;
import com.example.mpaz_pro_6springboot.common.mapper.ResumenMapper;
import com.example.mpaz_pro_6springboot.curriculo.dto.request.AlternativaRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.request.AlternativaUpdateRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.AlternativaResponse;
import com.example.mpaz_pro_6springboot.curriculo.model.Alternativa;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper MapStruct de Alternativa.
 *
 * <p>Las relaciones se mapean como referencias con solo el identificador;
 * el servicio las resuelve contra el repositorio antes de persistir.</p>
 */
@Mapper(
        componentModel = "spring",
        uses = {ResumenMapper.class, EntityRefMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AlternativaMapper {

    AlternativaResponse toResponse(Alternativa entity);

    List<AlternativaResponse> toResponseList(List<Alternativa> entities);

    @Mapping(source = "recursoId", target = "recurso")
    Alternativa toEntity(AlternativaRequest request);

    @Mapping(source = "recursoId", target = "recurso")
    Alternativa toEntity(AlternativaUpdateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "recurso", ignore = true)
    void updateEntity(AlternativaUpdateRequest request, @MappingTarget Alternativa entity);
}
