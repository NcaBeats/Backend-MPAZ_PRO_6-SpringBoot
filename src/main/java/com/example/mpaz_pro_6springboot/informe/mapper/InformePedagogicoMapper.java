package com.example.mpaz_pro_6springboot.informe.mapper;

import com.example.mpaz_pro_6springboot.common.mapper.EntityRefMapper;
import com.example.mpaz_pro_6springboot.common.mapper.ResumenMapper;
import com.example.mpaz_pro_6springboot.informe.dto.request.InformePedagogicoRequest;
import com.example.mpaz_pro_6springboot.informe.dto.request.InformePedagogicoUpdateRequest;
import com.example.mpaz_pro_6springboot.informe.dto.response.InformePedagogicoResponse;
import com.example.mpaz_pro_6springboot.informe.model.InformePedagogico;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper MapStruct de InformePedagogico.
 *
 * <p>Las relaciones se mapean como referencias con solo el identificador;
 * el servicio las resuelve contra el repositorio antes de persistir.</p>
 */
@Mapper(
        componentModel = "spring",
        uses = {ResumenMapper.class, EntityRefMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface InformePedagogicoMapper {

    InformePedagogicoResponse toResponse(InformePedagogico entity);

    List<InformePedagogicoResponse> toResponseList(List<InformePedagogico> entities);

    @Mapping(source = "usuarioId", target = "usuario")
    @Mapping(source = "unidadId", target = "unidad")
    InformePedagogico toEntity(InformePedagogicoRequest request);

    InformePedagogico toEntity(InformePedagogicoUpdateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "unidad", ignore = true)
    @Mapping(target = "fechaCorte", ignore = true)
    @Mapping(target = "origen", ignore = true)
    void updateEntity(InformePedagogicoUpdateRequest request, @MappingTarget InformePedagogico entity);
}
