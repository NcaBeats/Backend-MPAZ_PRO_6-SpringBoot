package com.example.mpaz_pro_6springboot.curriculo.alternativa.mapper;

import com.example.mpaz_pro_6springboot.curriculo.alternativa.dto.request.AlternativaCreateRequest;
import com.example.mpaz_pro_6springboot.curriculo.alternativa.dto.request.AlternativaUpdateRequest;
import com.example.mpaz_pro_6springboot.curriculo.alternativa.dto.response.AlternativaResponse;
import com.example.mpaz_pro_6springboot.curriculo.alternativa.model.Alternativa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AlternativaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pregunta", ignore = true)
    @Mapping(target = "respuestasEstudiante", ignore = true)
    Alternativa toEntity(AlternativaCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pregunta", ignore = true)
    @Mapping(target = "respuestasEstudiante", ignore = true)
    Alternativa toEntity(AlternativaUpdateRequest request);

    void updateEntity(AlternativaUpdateRequest request, @MappingTarget Alternativa entity);

    @Mapping(target = "preguntaId", source = "pregunta.id")
    AlternativaResponse toResponse(Alternativa entity);
}
