package com.example.mpaz_pro_6springboot.informe.mapper;

import com.example.mpaz_pro_6springboot.common.mapper.EntityRefMapper;
import com.example.mpaz_pro_6springboot.common.mapper.ResumenMapper;
import com.example.mpaz_pro_6springboot.informe.dto.request.DetalleInformePedagogicoRequest;
import com.example.mpaz_pro_6springboot.informe.dto.request.DetalleInformePedagogicoUpdateRequest;
import com.example.mpaz_pro_6springboot.informe.dto.response.DetalleInformePedagogicoResponse;
import com.example.mpaz_pro_6springboot.informe.model.DetalleInformePedagogico;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper MapStruct de DetalleInformePedagogico.
 *
 * <p>Las relaciones se mapean como referencias con solo el identificador;
 * el servicio las resuelve contra el repositorio antes de persistir.</p>
 */
@Mapper(
        componentModel = "spring",
        uses = {ResumenMapper.class, EntityRefMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface DetalleInformePedagogicoMapper {

    DetalleInformePedagogicoResponse toResponse(DetalleInformePedagogico entity);

    List<DetalleInformePedagogicoResponse> toResponseList(List<DetalleInformePedagogico> entities);

    @Mapping(source = "informePedagogicoId", target = "informePedagogico")
    @Mapping(source = "objetivoAprendizajeId", target = "objetivoAprendizaje")
    DetalleInformePedagogico toEntity(DetalleInformePedagogicoRequest request);

    DetalleInformePedagogico toEntity(DetalleInformePedagogicoUpdateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "informePedagogico", ignore = true)
    @Mapping(target = "objetivoAprendizaje", ignore = true)
    void updateEntity(DetalleInformePedagogicoUpdateRequest request, @MappingTarget DetalleInformePedagogico entity);
}
