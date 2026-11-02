package com.example.mpaz_pro_6springboot.curriculo.mapper;

import com.example.mpaz_pro_6springboot.common.mapper.EntityRefMapper;
import com.example.mpaz_pro_6springboot.common.mapper.ResumenMapper;
import com.example.mpaz_pro_6springboot.curriculo.dto.request.RecursoRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.request.RecursoUpdateRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.RecursoResponse;
import com.example.mpaz_pro_6springboot.curriculo.model.Recurso;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper MapStruct de Recurso.
 *
 * <p>Las relaciones se mapean como referencias con solo el identificador;
 * el servicio las resuelve contra el repositorio antes de persistir.</p>
 */
@Mapper(
        componentModel = "spring",
        uses = {ResumenMapper.class, EntityRefMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface RecursoMapper {

    RecursoResponse toResponse(Recurso entity);

    List<RecursoResponse> toResponseList(List<Recurso> entities);

    @Mapping(source = "tipoRecursoId", target = "tipoRecurso")
    @Mapping(source = "contenidoId", target = "contenido")
    Recurso toEntity(RecursoRequest request);

    @Mapping(source = "tipoRecursoId", target = "tipoRecurso")
    @Mapping(source = "contenidoId", target = "contenido")
    Recurso toEntity(RecursoUpdateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "tipoRecurso", ignore = true)
    @Mapping(target = "contenido", ignore = true)
    void updateEntity(RecursoUpdateRequest request, @MappingTarget Recurso entity);
}
