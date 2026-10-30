package com.example.mpaz_pro_6springboot.evaluacion.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mpaz_pro_6springboot.common.exception.ConflictoException;
import com.example.mpaz_pro_6springboot.common.exception.RecursoNoEncontradoException;
import com.example.mpaz_pro_6springboot.common.exception.ReglaNegocioException;
import com.example.mpaz_pro_6springboot.common.util.Validaciones;
import com.example.mpaz_pro_6springboot.curriculo.model.Actividad;
import com.example.mpaz_pro_6springboot.curriculo.repository.ActividadRepository;
import com.example.mpaz_pro_6springboot.evaluacion.model.Intento;
import com.example.mpaz_pro_6springboot.evaluacion.repository.IntentoRepository;
import com.example.mpaz_pro_6springboot.identidad.model.Usuario;
import com.example.mpaz_pro_6springboot.identidad.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de {@link Intento}.
 *
 * <p>Aplica las reglas de disponibilidad de la actividad y del limite de
 * intentos. {@code actividad.maxIntentos == null} significa sin limite.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IntentoService {

    private final IntentoRepository intentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ActividadRepository actividadRepository;

    public List<Intento> listar() {
        return intentoRepository.findAll();
    }

    public List<Intento> listarPorUsuario(Long usuarioId) {
        return intentoRepository.findAllByUsuarioId(usuarioId);
    }

    public List<Intento> listarPorActividad(Long actividadId) {
        return intentoRepository.findAllByActividadId(actividadId);
    }

    public List<Intento> listarPorUsuarioYActividad(Long usuarioId, Long actividadId) {
        return intentoRepository.findAllByUsuarioIdAndActividadId(usuarioId, actividadId);
    }

    public Intento obtener(Long id) {
        return intentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Intento", id));
    }

    public long contarPorUsuarioYActividad(Long usuarioId, Long actividadId) {
        return intentoRepository.countByUsuarioIdAndActividadId(usuarioId, actividadId);
    }

    @Transactional
    public Intento crear(Intento datos) {
        Validaciones.requerido(datos, "intento");

        Usuario usuario = resolverUsuario(datos.getUsuario());
        Actividad actividad = resolverActividad(datos.getActividad());

        Instant momento = datos.getFechaInicio() != null ? datos.getFechaInicio() : Instant.now();
        validarActividadDisponible(actividad, momento);
        validarLimiteIntentos(usuario, actividad);
        validarFechas(momento, datos.getFechaFin());

        datos.setId(null);
        datos.setUsuario(usuario);
        datos.setActividad(actividad);
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

    private void validarActividadDisponible(Actividad actividad, Instant momento) {
        if (!actividad.isHabilitada()) {
            throw new ReglaNegocioException("La actividad no esta habilitada");
        }
        if (actividad.getFechaInicio() != null && momento.isBefore(actividad.getFechaInicio())) {
            throw new ReglaNegocioException("La actividad aun no comienza");
        }
        if (actividad.getFechaTermino() != null && momento.isAfter(actividad.getFechaTermino())) {
            throw new ReglaNegocioException("La actividad ya termino");
        }
    }

    private void validarLimiteIntentos(Usuario usuario, Actividad actividad) {
        Integer maxIntentos = actividad.getMaxIntentos();
        if (maxIntentos == null) {
            return;
        }
        long usados = intentoRepository.countByUsuarioIdAndActividadId(
                usuario.getId(), actividad.getId());
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

    private Actividad resolverActividad(Actividad actividad) {
        if (actividad == null || actividad.getId() == null) {
            throw new ReglaNegocioException("El campo actividad es obligatorio");
        }
        return actividadRepository.findById(actividad.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Actividad", actividad.getId()));
    }
}
