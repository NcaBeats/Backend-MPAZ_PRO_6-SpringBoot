package com.example.mpaz_pro_6springboot.common.security;

import com.example.mpaz_pro_6springboot.docencia.repository.AsignaturaCursoRepository;
import com.example.mpaz_pro_6springboot.docencia.repository.UsuarioCursoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Reglas de acceso a informes pedagogicos y detalles.
 *
 * <p>UTP ve todo; un estudiante solo sus datos; un docente solo estudiantes
 * inscritos en cursos donde tiene alguna asignatura.</p>
 */
@Component
@Transactional(readOnly = true)
public class ReportAccessPolicy {

    private final UsuarioCursoRepository usuarioCursoRepository;
    private final AsignaturaCursoRepository asignaturaCursoRepository;

    public ReportAccessPolicy(
            UsuarioCursoRepository usuarioCursoRepository,
            AsignaturaCursoRepository asignaturaCursoRepository
    ) {
        this.usuarioCursoRepository = usuarioCursoRepository;
        this.asignaturaCursoRepository = asignaturaCursoRepository;
    }

    public boolean puedeVerEstudiante(Long estudianteId) {
        if (estudianteId == null) {
            return false;
        }
        if (SecurityUtils.isUtp()) {
            return true;
        }
        if (SecurityUtils.isEstudiante()) {
            return SecurityUtils.currentUsuario()
                    .map(usuario -> estudianteId.equals(usuario.getId()))
                    .orElse(false);
        }
        if (!SecurityUtils.isDocente()) {
            return false;
        }
        Long docenteId = SecurityUtils.requireUsuario().getId();
        return usuarioCursoRepository.findAllByUsuarioId(estudianteId).stream()
                .map(participacion -> participacion.getCurso().getId())
                .anyMatch(cursoId -> asignaturaCursoRepository
                        .existsByCursoIdAndDocenteId(cursoId, docenteId));
    }

    public void requirePuedeVerEstudiante(Long estudianteId, String recurso) {
        if (!puedeVerEstudiante(estudianteId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No tienes acceso a " + recurso + " de este estudiante");
        }
    }
}
