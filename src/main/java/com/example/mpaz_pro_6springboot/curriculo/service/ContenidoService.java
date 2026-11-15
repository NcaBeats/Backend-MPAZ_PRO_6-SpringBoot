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
import com.example.mpaz_pro_6springboot.curriculo.model.Contenido;
import com.example.mpaz_pro_6springboot.curriculo.model.EstadoAutorizacion;
import com.example.mpaz_pro_6springboot.curriculo.model.Unidad;
import com.example.mpaz_pro_6springboot.curriculo.repository.ContenidoRepository;
import com.example.mpaz_pro_6springboot.curriculo.repository.EstadoAutorizacionRepository;
import com.example.mpaz_pro_6springboot.curriculo.repository.UnidadRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de {@link Contenido}.
 *
 * <p>La unidad del contenido es inmutable: el trigger de PostgreSQL rechaza
 * cualquier cambio y este servicio lo valida antes de llegar a la base de
 * datos. Para reubicar un contenido debe crearse uno nuevo en la unidad
 * destino.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContenidoService {

    /** Id del estado AUTORIZADO en el catalogo sembrado. */
    private static final Long ESTADO_AUTORIZADO = 3L;

    private final ContenidoRepository contenidoRepository;
    private final UnidadRepository unidadRepository;
    private final EstadoAutorizacionRepository estadoAutorizacionRepository;
    private final MaterialAccessPolicy materialAccessPolicy;

    public List<Contenido> listar() {
        return filtrarParaEstudiante(contenidoRepository.findAll());
    }

    public List<Contenido> listarPorUnidad(Long unidadId) {
        return filtrarParaEstudiante(contenidoRepository.findAllByUnidadId(unidadId));
    }

    public List<Contenido> listarPorEstado(Long estadoId) {
        return filtrarParaEstudiante(contenidoRepository.findAllByEstadoId(estadoId));
    }

    public List<Contenido> listarPorUnidadYEstado(Long unidadId, Long estadoId) {
        return filtrarParaEstudiante(
                contenidoRepository.findAllByUnidadIdAndEstadoId(unidadId, estadoId));
    }

    public Contenido obtener(Long id) {
        Contenido contenido = contenidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Contenido", id));
        if (!esVisibleParaEstudiante(contenido)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "El contenido no esta disponible para estudiantes");
        }
        return contenido;
    }

    @Transactional
    public Contenido crear(Contenido datos) {
        Validaciones.requerido(datos, "contenido");

        Unidad unidad = resolverUnidad(datos.getUnidad());
        materialAccessPolicy.requirePuedeGestionarUnidad(unidad);
        EstadoAutorizacion estado = resolverEstado(datos.getEstado());

        return contenidoRepository.save(Contenido.builder()
                .descripcion(Validaciones.textoOpcional(datos.getDescripcion()))
                .estado(estado)
                .unidad(unidad)
                .build());
    }

    @Transactional
    public Contenido actualizar(Long id, Contenido datos) {
        Validaciones.requerido(datos, "contenido");
        Contenido contenido = obtener(id);
        materialAccessPolicy.requirePuedeGestionarContenido(contenido);

        if (datos.getUnidad() != null && datos.getUnidad().getId() != null
                && !datos.getUnidad().getId().equals(contenido.getUnidad().getId())) {
            throw new ReglaNegocioException(
                    "La unidad del contenido es inmutable; cree un contenido nuevo en la unidad destino");
        }
        if (datos.getDescripcion() != null) {
            contenido.setDescripcion(Validaciones.textoOpcional(datos.getDescripcion()));
        }
        if (datos.getEstado() != null) {
            contenido.setEstado(resolverEstado(datos.getEstado()));
        }
        return contenido;
    }

    @Transactional
    public void eliminar(Long id) {
        Contenido contenido = obtener(id);
        materialAccessPolicy.requirePuedeGestionarContenido(contenido);
        contenidoRepository.delete(contenido);
    }

    private Unidad resolverUnidad(Unidad unidad) {
        if (unidad == null || unidad.getId() == null) {
            throw new ReglaNegocioException("El campo unidad es obligatorio");
        }
        return unidadRepository.findById(unidad.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Unidad", unidad.getId()));
    }

    private EstadoAutorizacion resolverEstado(EstadoAutorizacion estado) {
        if (estado == null || estado.getId() == null) {
            throw new ReglaNegocioException("El campo estado es obligatorio");
        }
        return estadoAutorizacionRepository.findById(estado.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "EstadoAutorizacion", estado.getId()));
    }

    private List<Contenido> filtrarParaEstudiante(List<Contenido> contenidos) {
        if (!SecurityUtils.isEstudiante()) {
            return contenidos;
        }
        return contenidos.stream()
                .filter(this::esVisibleParaEstudiante)
                .toList();
    }

    private boolean esVisibleParaEstudiante(Contenido contenido) {
        if (!SecurityUtils.isEstudiante()) {
            return true;
        }
        if (contenido.getUnidad() == null || contenido.getUnidad().getEstado() == null) {
            return false;
        }
        return ESTADO_AUTORIZADO.equals(contenido.getUnidad().getEstado().getId());
    }
}
