package com.example.mpaz_pro_6springboot.evaluacion.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mpaz_pro_6springboot.common.exception.ConflictoException;
import com.example.mpaz_pro_6springboot.common.exception.RecursoNoEncontradoException;
import com.example.mpaz_pro_6springboot.common.exception.ReglaNegocioException;
import com.example.mpaz_pro_6springboot.common.util.Validaciones;
import com.example.mpaz_pro_6springboot.docencia.model.ActividadCurso;
import com.example.mpaz_pro_6springboot.docencia.repository.ActividadCursoRepository;
import com.example.mpaz_pro_6springboot.docencia.repository.UsuarioCursoRepository;
import com.example.mpaz_pro_6springboot.evaluacion.model.Intento;
import com.example.mpaz_pro_6springboot.evaluacion.repository.IntentoRepository;
import com.example.mpaz_pro_6springboot.identidad.model.Usuario;
import com.example.mpaz_pro_6springboot.identidad.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de {@link Intento}.
 *
 * <p>El intento se crea contra una asignacion {@code actividad_curso}. Se
 * valida que la asignacion exista, que el alumno pertenezca al curso, que la
 * actividad este habilitada y dentro de su ventana, y que no se supere el
 * limite de intentos de esa asignacion. La visibilidad ({@code visible}) es
 * una decision de presentacion del frontend.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IntentoService {

    private final IntentoRepository intentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ActividadCursoRepository actividadCursoRepository;
    private final UsuarioCursoRepository usuarioCursoRepository;

    public List<Intento> listar() {
        return intentoRepository.findAll();
    }

    public List<Intento> listarPorUsuario(Long usuarioId) {
        return intentoRepository.findAllByUsuarioId(usuarioId);
    }

    public List<Intento> listarPorActividad(Long actividadId) {
        return intentoRepository.findAllByActividadCursoActividadId(actividadId);
    }

    public List<Intento> listarPorActividadCurso(Long actividadCursoId) {
        return intentoRepository.findAllByActividadCursoId(actividadCursoId);
    }

    public List<Intento> listarPorUsuarioYActividad(Long usuarioId, Long actividadId) {
        return intentoRepository.findAllByUsuarioIdAndActividadCursoActividadId(
                usuarioId, actividadId);
    }

    public List<Intento> listarPorUsuarioYActividadCurso(Long usuarioId, Long actividadCursoId) {
        return intentoRepository.findAllByUsuarioIdAndActividadCursoId(
                usuarioId, actividadCursoId);
    }

    public Intento obtener(Long id) {
        return intentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Intento", id));
    }

    public long contarPorUsuarioYActividadCurso(Long usuarioId, Long actividadCursoId) {
        return intentoRepository.countByUsuarioIdAndActividadCursoId(
                usuarioId, actividadCursoId);
    }

    @Transactional
    public Intento crear(Intento datos) {
        Validaciones.requerido(datos, "intento");

        Usuario usuario = resolverUsuario(datos.getUsuario());
        ActividadCurso asignacion = resolverActividadCurso(datos.getActividadCurso());

        Instant momento = datos.getFechaInicio() != null ? datos.getFechaInicio() : Instant.now();
        validarInscripcion(usuario, asignacion);
        validarDisponible(asignacion, momento);
        validarLimiteIntentos(usuario, asignacion);
        validarFechas(momento, datos.getFechaFin());

        datos.setId(null);
        datos.setUsuario(usuario);
        datos.setActividadCurso(asignacion);
        datos.setFechaInicio(momento);
        return intentoRepository.save(datos);
    }

    /**
     * Cierra un intento abierto registrando la fecha de termino.
     */
    @Transactional
    public Intento cerrar(Long id) {
        Intento intento = obtener(id);
        if (intento.getFechaFin() != null) {
            throw new ConflictoException("El intento ya esta cerrado");
        }
        intento.setFechaFin(Instant.now());
        return intento;
    }

    @Transactional
    public void eliminar(Long id) {
        intentoRepository.delete(obtener(id));
    }

    private void validarInscripcion(Usuario usuario, ActividadCurso asignacion) {
        if (!usuarioCursoRepository.existsByUsuarioIdAndCursoId(
                usuario.getId(), asignacion.getCurso().getId())) {
            throw new ReglaNegocioException(
                    "El usuario no esta asociado al curso de la actividad");
        }
    }

    private void validarDisponible(ActividadCurso asignacion, Instant momento) {
        if (!asignacion.isHabilitada()) {
            throw new ReglaNegocioException("La actividad no esta habilitada en el curso");
        }
        if (asignacion.getFechaInicio() != null && momento.isBefore(asignacion.getFechaInicio())) {
            throw new ReglaNegocioException("La actividad aun no comienza");
        }
        if (asignacion.getFechaFin() != null && momento.isAfter(asignacion.getFechaFin())) {
            throw new ReglaNegocioException("La actividad ya termino");
        }
    }

    private void validarLimiteIntentos(Usuario usuario, ActividadCurso asignacion) {
        Integer maxIntentos = asignacion.getMaxIntentos();
        if (maxIntentos == null) {
            return;
        }
        long usados = intentoRepository.countByUsuarioIdAndActividadCursoId(
                usuario.getId(), asignacion.getId());
        if (usados >= maxIntentos) {
            throw new ConflictoException(
                    "Se alcanzo el maximo de intentos permitidos (" + maxIntentos + ")");
        }
    }

    private void validarFechas(Instant inicio, Instant fin) {
        if (inicio != null && fin != null && fin.isBefore(inicio)) {
            throw new ReglaNegocioException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }
    }

    private Usuario resolverUsuario(Usuario usuario) {
        if (usuario == null || usuario.getId() == null) {
            throw new ReglaNegocioException("El campo usuario es obligatorio");
        }
        return usuarioRepository.findById(usuario.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", usuario.getId()));
    }

    private ActividadCurso resolverActividadCurso(ActividadCurso asignacion) {
        if (asignacion == null || asignacion.getId() == null) {
            throw new ReglaNegocioException("El campo actividadCurso es obligatorio");
        }
        return actividadCursoRepository.findById(asignacion.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "ActividadCurso", asignacion.getId()));
    }
}
