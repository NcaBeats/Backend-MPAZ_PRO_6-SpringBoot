package com.example.mpaz_pro_6springboot.informe.informe_pedagogico;

import com.example.mpaz_pro_6springboot.informe.informe_pedagogico.dto.request.InformePedagogicoCreateRequest;
import com.example.mpaz_pro_6springboot.informe.informe_pedagogico.dto.request.InformePedagogicoUpdateRequest;
import com.example.mpaz_pro_6springboot.informe.informe_pedagogico.dto.response.InformePedagogicoResponse;
import com.example.mpaz_pro_6springboot.informe.informe_pedagogico.model.InformePedagogico;
import com.example.mpaz_pro_6springboot.curriculo.unidad.mapper.UnidadMapper;
import com.example.mpaz_pro_6springboot.identidad.usuario.mapper.UsuarioMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {UsuarioMapper.class, UnidadMapper.class})
public interface InformePedagogicoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estudiante", ignore = true)
    @Mapping(target = "unidad", ignore = true)
    @Mapping(target = "docente", ignore = true)
    @Mapping(target = "fechaGeneracion", ignore = true)
    @Mapping(target = "fechaValidacion", ignore = true)
    @Mapping(target = "detallesOa", ignore = true)
    InformePedagogico toEntity(InformePedagogicoCreateRequest request);

    void updateEntity(InformePedagogicoUpdateRequest request, @MappingTarget InformePedagogico entity);

    InformePedagogicoResponse toResponse(InformePedagogico entity);
}
