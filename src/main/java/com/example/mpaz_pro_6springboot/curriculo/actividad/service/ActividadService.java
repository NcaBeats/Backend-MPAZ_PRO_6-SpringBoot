package com.example.mpaz_pro_6springboot.curriculo.actividad.service;

import com.example.mpaz_pro_6springboot.common.enums.TipoActividad;
import com.example.mpaz_pro_6springboot.common.security.CurriculoAuthoringPolicy;
import com.example.mpaz_pro_6springboot.common.security.StudentVisibilityService;
import com.example.mpaz_pro_6springboot.curriculo.actividad.model.Actividad;
import com.example.mpaz_pro_6springboot.curriculo.actividad.repository.ActividadRepository;
import com.example.mpaz_pro_6springboot.curriculo.contenido.model.Contenido;
import com.example.mpaz_pro_6springboot.curriculo.contenido.repository.ContenidoRepository;
import com.example.mpaz_pro_6springboot.curriculo.pregunta.repository.PreguntaRepository;
import com.example.mpaz_pro_6springboot.curriculo.unidad.model.Unidad;
import com.example.mpaz_pro_6springboot.curriculo.unidad.repository.UnidadRepository;
import com.example.mpaz_pro_6springboot.docencia.asignacion_actividad.repository.AsignacionActividadRepository;
import com.example.mpaz_pro_6springboot.evaluacion.intento.repository.IntentoRepository;
import com.example.mpaz_pro_6springboot.identidad.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActividadService {

    private static final int ORDEN_PREDETERMINADO = 1;

    private final ActividadRepository actividadRepository;
    private final UnidadRepository unidadRepository;
    private final ContenidoRepository contenidoRepository;
    private final PreguntaRepository preguntaRepository;
    private final AsignacionActividadRepository asignacionActividadRepository;
    private final IntentoRepository intentoRepository;
    private final CurriculoAuthoringPolicy authoringPolicy;
    private final StudentVisibilityService visibilityService;

    public List<Actividad> findAll(
            Long unidadId,
            Long contenidoId,
            Boolean soloTransversales,
            Usuario currentUser
    ) {
        boolean soloTransversal = Boolean.TRUE.equals(soloTransversales);

        if (contenidoId != null && soloTransversal) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede filtrar por contenido y por transversales a la vez"
            );
        }

        if ((contenidoId != null || soloTransversal) && unidadId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El filtro unidadId es obligatorio para contenidoId o soloTransversales"
            );
        }

        if (unidadId != null) {
            getUnidad(unidadId);
        }

        if (contenidoId != null) {
            validateContenidoDeUnidad(getContenido(contenidoId), unidadId);
        }

        List<Actividad> actividades;
        if (contenidoId != null) {
            actividades = actividadRepository
                    .findByUnidadIdAndContenidoIdOrderByOrdenAsc(
                            unidadId,
                            contenidoId
                    );
        } else if (soloTransversal) {
            actividades = actividadRepository
                    .findByUnidadIdAndContenidoIsNullOrderByOrdenAsc(unidadId);
        } else {
            actividades = unidadId == null
                    ? actividadRepository.findAllByOrderByUnidadIdAscOrdenAscIdAsc()
                    : actividadRepository.findByUnidadIdOrderByOrdenAsc(unidadId);
        }

        if (!visibilityService.isStudent(currentUser)) {
            return actividades;
        }

        var visibles = visibilityService.visibleActivityIds(currentUser);
        return actividades.stream()
                .filter(actividad -> visibles.contains(actividad.getId()))
                .toList();
    }

    public Actividad findById(Long id, Usuario currentUser) {
        Actividad actividad = getActividad(id);
        if (!visibilityService.canSeeActivity(currentUser, actividad)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "La actividad no está asignada al curso del estudiante"
            );
        }
        return actividad;
    }

    @Transactional
    public Actividad create(
            Actividad actividad,
            Long unidadId,
            Long contenidoId,
            Usuario currentUser
    ) {
        Unidad unidad = getUnidad(unidadId);
        authoringPolicy.assertCanAuthor(currentUser, unidad);
        if (actividad.getOrden() == null) {
            actividad.setOrden(ORDEN_PREDETERMINADO);
        }
        validateTituloUnico(unidadId, actividad.getTitulo(), null);
        validatePruebaFinalUnica(unidadId, actividad.getTipo(), null);
        actividad.setUnidad(unidad);

        if (contenidoId != null) {
            Contenido contenido = getContenido(contenidoId);
            validateContenidoDeUnidad(contenido, unidadId);
            actividad.setContenido(contenido);
        }

        return actividadRepository.save(actividad);
    }

    @Transactional
    public Actividad update(
            Long id,
            Actividad cambios,
            Long contenidoId,
            Boolean desasociarContenido
    ) {
        boolean desasociar = Boolean.TRUE.equals(desasociarContenido);

        if (contenidoId != null && desasociar) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede asociar y desasociar el contenido a la vez"
            );
        }

        if (isEmptyUpdate(cambios, contenidoId, desasociar)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe proporcionar al menos un campo para actualizar"
            );
        }

        Actividad actividad = getActividad(id);
        Long unidadId = actividad.getUnidad().getId();
        if (cambios.getTitulo() != null) {
            validateTituloUnico(unidadId, cambios.getTitulo(), id);
            actividad.setTitulo(cambios.getTitulo());
        }
        if (cambios.getTipo() != null) {
            validatePruebaFinalUnica(unidadId, cambios.getTipo(), id);
            actividad.setTipo(cambios.getTipo());
        }
        if (cambios.getOrden() != null) {
            actividad.setOrden(cambios.getOrden());
        }
        if (desasociar) {
            actividad.setContenido(null);
        } else if (contenidoId != null) {
            Contenido contenido = getContenido(contenidoId);
            validateContenidoDeUnidad(contenido, unidadId);
            actividad.setContenido(contenido);
        }
        return actividadRepository.save(actividad);
    }

    @Transactional
    public void delete(Long id) {
        Actividad actividad = getActividad(id);
        if (preguntaRepository.existsByActividadId(id)
                || asignacionActividadRepository.existsByActividadId(id)
                || intentoRepository.existsByActividadId(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar una actividad que tiene preguntas, asignaciones o intentos asociados"
            );
        }
        actividadRepository.delete(actividad);
    }

    private boolean isEmptyUpdate(
            Actividad cambios,
            Long contenidoId,
            boolean desasociarContenido
    ) {
        return cambios.getTitulo() == null
                && cambios.getTipo() == null
                && cambios.getOrden() == null
                && contenidoId == null
                && !desasociarContenido;
    }

    private void validateTituloUnico(Long unidadId, String titulo, Long excludedId) {
        boolean exists = excludedId == null
                ? actividadRepository.existsByUnidadIdAndTitulo(unidadId, titulo)
                : actividadRepository.existsByUnidadIdAndTituloAndIdNot(unidadId, titulo, excludedId);
        if (exists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe una actividad con ese título en la unidad"
            );
        }
    }

    private void validatePruebaFinalUnica(Long unidadId, TipoActividad tipo, Long excludedId) {
        if (tipo != TipoActividad.PRUEBA_FINAL) {
            return;
        }
        boolean exists = excludedId == null
                ? actividadRepository.existsByUnidadIdAndTipo(unidadId, TipoActividad.PRUEBA_FINAL)
                : actividadRepository.existsByUnidadIdAndTipoAndIdNot(unidadId, TipoActividad.PRUEBA_FINAL, excludedId);
        if (exists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La unidad ya tiene una prueba final"
            );
        }
    }

    private void validateContenidoDeUnidad(Contenido contenido, Long unidadId) {
        Long unidadDelContenido = contenido.getOa().getUnidad().getId();
        if (!unidadDelContenido.equals(unidadId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El contenido pertenece a otra unidad"
            );
        }
    }

    private Actividad getActividad(Long id) {
        return actividadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Actividad no encontrada"));
    }

    private Unidad getUnidad(Long id) {
        return unidadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada"));
    }

    private Contenido getContenido(Long id) {
        return contenidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Contenido no encontrado"
                ));
    }
}
