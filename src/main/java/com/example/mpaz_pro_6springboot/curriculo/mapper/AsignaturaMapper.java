package com.example.mpaz_pro_6springboot.curriculo.mapper;

import com.example.mpaz_pro_6springboot.common.mapper.EntityRefMapper;
import com.example.mpaz_pro_6springboot.common.mapper.ResumenMapper;
import com.example.mpaz_pro_6springboot.curriculo.dto.request.AsignaturaRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.request.AsignaturaUpdateRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.AsignaturaResponse;
import com.example.mpaz_pro_6springboot.curriculo.model.Asignatura;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper MapStruct de Asignatura.
 *
 * <p>Las relaciones se mapean como referencias con solo el identificador;
 * el servicio las resuelve contra el repositorio antes de persistir.</p>
 */
@Mapper(
        componentModel = "spring",
        uses = {ResumenMapper.class, EntityRefMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AsignaturaMapper {

    AsignaturaResponse toResponse(Asignatura entity);

    List<AsignaturaResponse> toResponseList(List<Asignatura> entities);

    Asignatura toEntity(AsignaturaRequest request);

    Asignatura toEntity(AsignaturaUpdateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(AsignaturaUpdateRequest request, @MappingTarget Asignatura entity);
}
