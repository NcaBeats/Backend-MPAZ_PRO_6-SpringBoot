package com.example.mpaz_pro_6springboot.informe.detalle_informe_oa;

import com.example.mpaz_pro_6springboot.informe.detalle_informe_oa.dto.request.DetalleInformeOaCreateRequest;
import com.example.mpaz_pro_6springboot.informe.detalle_informe_oa.dto.request.DetalleInformeOaUpdateRequest;
import com.example.mpaz_pro_6springboot.informe.detalle_informe_oa.dto.response.DetalleInformeOaResponse;
import com.example.mpaz_pro_6springboot.informe.detalle_informe_oa.model.DetalleInformeOa;
import com.example.mpaz_pro_6springboot.informe.informe_pedagogico.mapper.InformePedagogicoMapper;
import com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.mapper.ObjetivoAprendizajeMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {InformePedagogicoMapper.class, ObjetivoAprendizajeMapper.class})
public interface DetalleInformeOaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "informe", ignore = true)
    @Mapping(target = "oa", ignore = true)
    DetalleInformeOa toEntity(DetalleInformeOaCreateRequest request);

    void updateEntity(DetalleInformeOaUpdateRequest request, @MappingTarget DetalleInformeOa entity);

    DetalleInformeOaResponse toResponse(DetalleInformeOa entity);

    @Mapping(target = "informeId", source = "informe.id")
    @Mapping(target = "oaId", source = "oa.id")
    DetalleInformeOaResponse toResponse(DetalleInformeOa entity);
}
