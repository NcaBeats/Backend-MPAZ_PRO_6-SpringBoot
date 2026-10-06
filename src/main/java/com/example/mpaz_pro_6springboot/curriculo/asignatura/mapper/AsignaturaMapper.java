package com.example.mpaz_pro_6springboot.curriculo.asignatura;

import com.example.mpaz_pro_6springboot.curriculo.asignatura.dto.request.AsignaturaCreateRequest;
import com.example.mpaz_pro_6springboot.curriculo.asignatura.dto.request.AsignaturaUpdateRequest;
import com.example.mpaz_pro_6springboot.curriculo.asignatura.dto.response.AsignaturaResponse;
import com.example.mpaz_pro_6springboot.curriculo.asignatura.model.Asignatura;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AsignaturaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "unidades", ignore = true)
    Asignatura toEntity(AsignaturaCreateRequest request);

    void updateEntity(AsignaturaUpdateRequest request, @MappingTarget Asignatura entity);

    AsignaturaResponse toResponse(Asignatura entity);
}
