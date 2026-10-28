package com.example.mpaz_pro_6springboot.common.security;

import com.example.mpaz_pro_6springboot.common.enums.Rol;
import com.example.mpaz_pro_6springboot.curriculo.actividad.model.Actividad;
import com.example.mpaz_pro_6springboot.curriculo.actividad.repository.ActividadRepository;
import com.example.mpaz_pro_6springboot.docencia.asignacion.model.Asignacion;
import com.example.mpaz_pro_6springboot.docencia.asignacion.repository.AsignacionRepository;
import com.example.mpaz_pro_6springboot.docencia.asignacion_actividad.repository.AsignacionActividadRepository;
import com.example.mpaz_pro_6springboot.docencia.asignacion_oa.repository.AsignacionOaRepository;
import com.example.mpaz_pro_6springboot.identidad.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Resuelve que OAs y actividades puede ver un estudiante.
 *
 * <p>La visibilidad se deriva de la asignación de su curso:</p>
 * <ul>
 *   <li>los OAs salen de {@code asignacion_oa};</li>
 *   <li>las actividades salen de {@code asignacion_actividad};</li>
 *   <li>la prueba final se agrega solo si la asignación la habilita.</li>
 * </ul>
 *
 * <p>UTP y docentes conservan acceso completo.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentVisibilityService {

    private final AsignacionRepository asignacionRepository;
    private final AsignacionOaRepository asignacionOaRepository;
    private final AsignacionActividadRepository asignacionActividadRepository;
    private final ActividadRepository actividadRepository;

    /** Indica si el usuario es un estudiante con curso asignado. */
    public boolean isStudent(Usuario usuario) {
        return usuario != null && usuario.getRol() == Rol.ESTUDIANTE;
    }

    /** OAs visibles para el estudiante. */
    public Set<Long> visibleObjectiveIds(Usuario usuario) {
        Set<Long> ids = new HashSet<>();
        for (Asignacion asignacion : asignacionesDelEstudiante(usuario)) {
            asignacionOaRepository.findByAsignacionId(asignacion.getId())
                    .forEach(link -> ids.add(link.getObjetivoAprendizaje().getId()));
        }
        return ids;
    }

    /** Actividades visibles para el estudiante. */
    public Set<Long> visibleActivityIds(Usuario usuario) {
        Set<Long> ids = new HashSet<>();
        for (Asignacion asignacion : asignacionesDelEstudiante(usuario)) {
            asignacionActividadRepository.findByAsignacionId(asignacion.getId())
                    .forEach(link -> ids.add(link.getActividad().getId()));
            if (Boolean.TRUE.equals(asignacion.getPruebaFinalHabilitada())) {
                actividadRepository
                        .findPruebaFinalByUnidadId(asignacion.getUnidad().getId())
                        .ifPresent(prueba -> ids.add(prueba.getId()));
            }
        }
        return ids;
    }

    /** Indica si el OA es visible para el usuario. */
    public boolean canSeeObjective(Usuario usuario, Long objectiveId) {
        return !isStudent(usuario) || visibleObjectiveIds(usuario).contains(objectiveId);
    }

    /** Indica si la actividad es visible para el usuario. */
    public boolean canSeeActivity(Usuario usuario, Long activityId) {
        return !isStudent(usuario) || visibleActivityIds(usuario).contains(activityId);
    }

    /** Indica si la actividad es visible para el usuario. */
    public boolean canSeeActivity(Usuario usuario, Actividad actividad) {
        return actividad != null && canSeeActivity(usuario, actividad.getId());
    }

    private List<Asignacion> asignacionesDelEstudiante(Usuario usuario) {
        if (!isStudent(usuario) || usuario.getCurso() == null) {
            return List.of();
        }
        return asignacionRepository.findByCursoId(usuario.getCurso().getId());
    }
}
