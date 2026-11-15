package com.example.mpaz_pro_6springboot.curriculo.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.mpaz_pro_6springboot.common.exception.RecursoNoEncontradoException;
import com.example.mpaz_pro_6springboot.common.exception.ReglaNegocioException;
import com.example.mpaz_pro_6springboot.common.security.MaterialAccessPolicy;
import com.example.mpaz_pro_6springboot.common.security.SecurityUtils;
import com.example.mpaz_pro_6springboot.common.util.Validaciones;
import com.example.mpaz_pro_6springboot.curriculo.model.Asignatura;
import com.example.mpaz_pro_6springboot.curriculo.model.EstadoAutorizacion;
import com.example.mpaz_pro_6springboot.curriculo.model.Unidad;
import com.example.mpaz_pro_6springboot.curriculo.repository.AsignaturaRepository;
import com.example.mpaz_pro_6springboot.curriculo.repository.EstadoAutorizacionRepository;
import com.example.mpaz_pro_6springboot.curriculo.repository.UnidadRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de {@link Unidad}.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UnidadService {

    /** Id del estado AUTORIZADO en el catalogo sembrado. */
    private static final Long ESTADO_AUTORIZADO = 3L;

    private final UnidadRepository unidadRepository;
    private final AsignaturaRepository asignaturaRepository;
    private final EstadoAutorizacionRepository estadoAutorizacionRepository;
    private final MaterialAccessPolicy materialAccessPolicy;

    public List<Unidad> listar() {
        return filtrarParaEstudiante(unidadRepository.findAll());
    }

    public List<Unidad> listarPorAsignatura(Long asignaturaId) {
        return filtrarParaEstudiante(unidadRepository.findAllByAsignaturaId(asignaturaId));
    }

    public List<Unidad> listarPorEstado(Long estadoId) {
        return filtrarParaEstudiante(unidadRepository.findAllByEstadoId(estadoId));
    }

    public List<Unidad> listarPorAsignaturaYEstado(Long asignaturaId, Long estadoId) {
        return filtrarParaEstudiante(
                unidadRepository.findAllByAsignaturaIdAndEstadoId(asignaturaId, estadoId));
    }

    public Unidad obtener(Long id) {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Unidad", id));
        if (!esVisibleParaEstudiante(unidad)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "La unidad no esta disponible para estudiantes");
        }
        return unidad;
    }

    @Transactional
    public Unidad crear(Unidad datos) {
        Validaciones.requerido(datos, "unidad");

        String nombre = Validaciones.textoObligatorio(datos.getNombre(), "nombre");
        Asignatura asignatura = resolverAsignatura(datos.getAsignatura());
        materialAccessPolicy.requirePuedeGestionarAsignatura(asignatura.getId());
        EstadoAutorizacion estado = resolverEstado(datos.getEstado());

        datos.setId(null);
        datos.setNombre(nombre);
        datos.setDescripcion(Validaciones.textoOpcional(datos.getDescripcion()));
        datos.setAsignatura(asignatura);
        datos.setEstado(estado);
        return unidadRepository.save(datos);
    }

    @Transactional
    public Unidad actualizar(Long id, Unidad datos) {
        Validaciones.requerido(datos, "unidad");
        Unidad unidad = obtener(id);
        materialAccessPolicy.requirePuedeGestionarUnidad(unidad);

        if (datos.getNombre() != null) {
            unidad.setNombre(Validaciones.textoObligatorio(datos.getNombre(), "nombre"));
        }
        if (datos.getDescripcion() != null) {
            unidad.setDescripcion(Validaciones.textoOpcional(datos.getDescripcion()));
        }
        if (datos.getAsignatura() != null) {
            Asignatura nuevaAsignatura = resolverAsignatura(datos.getAsignatura());
            materialAccessPolicy.requirePuedeGestionarAsignatura(nuevaAsignatura.getId());
            unidad.setAsignatura(nuevaAsignatura);
        }
        if (datos.getEstado() != null) {
            unidad.setEstado(resolverEstado(datos.getEstado()));
        }
        return unidad;
    }

    @Transactional
    public void eliminar(Long id) {
        Unidad unidad = obtener(id);
        materialAccessPolicy.requirePuedeGestionarUnidad(unidad);
        unidadRepository.delete(unidad);
    }

    private Asignatura resolverAsignatura(Asignatura asignatura) {
        if (asignatura == null || asignatura.getId() == null) {
            throw new ReglaNegocioException("El campo asignatura es obligatorio");
        }
        return asignaturaRepository.findById(asignatura.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Asignatura", asignatura.getId()));
    }

    private EstadoAutorizacion resolverEstado(EstadoAutorizacion estado) {
        if (estado == null || estado.getId() == null) {
            throw new ReglaNegocioException("El campo estado es obligatorio");
        }
        return estadoAutorizacionRepository.findById(estado.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("EstadoAutorizacion", estado.getId()));
    }

    private List<Unidad> filtrarParaEstudiante(List<Unidad> unidades) {
        if (!SecurityUtils.isEstudiante()) {
            return unidades;
        }
        return unidades.stream()
                .filter(this::esVisibleParaEstudiante)
                .toList();
    }

    private boolean esVisibleParaEstudiante(Unidad unidad) {
        if (!SecurityUtils.isEstudiante()) {
            return true;
        }
        return unidad.getEstado() != null
                && ESTADO_AUTORIZADO.equals(unidad.getEstado().getId());
    }
}
