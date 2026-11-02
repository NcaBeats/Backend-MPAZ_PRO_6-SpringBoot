package com.example.mpaz_pro_6springboot.docencia.mapper;

import com.example.mpaz_pro_6springboot.common.mapper.EntityRefMapper;
import com.example.mpaz_pro_6springboot.common.mapper.ResumenMapper;
import com.example.mpaz_pro_6springboot.docencia.dto.request.UsuarioCursoRequest;
import com.example.mpaz_pro_6springboot.docencia.dto.response.UsuarioCursoResponse;
import com.example.mpaz_pro_6springboot.docencia.model.UsuarioCurso;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper MapStruct de UsuarioCurso.
 *
 * <p>Las relaciones se mapean como referencias con solo el identificador;
 * el servicio las resuelve contra el repositorio antes de persistir.</p>
 */
@Mapper(
        componentModel = "spring",
        uses = {ResumenMapper.class, EntityRefMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UsuarioCursoMapper {

    UsuarioCursoResponse toResponse(UsuarioCurso entity);

    List<UsuarioCursoResponse> toResponseList(List<UsuarioCurso> entities);

    @Mapping(source = "usuarioId", target = "usuario")
    @Mapping(source = "cursoId", target = "curso")
    UsuarioCurso toEntity(UsuarioCursoRequest request);
}
