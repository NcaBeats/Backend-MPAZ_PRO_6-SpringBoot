package com.example.mpaz_pro_6springboot.curriculo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mpaz_pro_6springboot.common.exception.RecursoNoEncontradoException;
import com.example.mpaz_pro_6springboot.common.exception.ReglaNegocioException;
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

    private final ContenidoRepository contenidoRepository;
    private final UnidadRepository unidadRepository;
    private final EstadoAutorizacionRepository estadoAutorizacionRepository;

    public List<Contenido> listar() {
        return contenidoRepository.findAll();
    }

    public List<Contenido> listarPorUnidad(Long unidadId) {
        return contenidoRepository.findAllByUnidadId(unidadId);
    }

    public List<Contenido> listarPorEstado(Long estadoId) {
        return contenidoRepository.findAllByEstadoId(estadoId);
    }

    public List<Contenido> listarPorUnidadYEstado(Long unidadId, Long estadoId) {
        return contenidoRepository.findAllByUnidadIdAndEstadoId(unidadId, estadoId);
    }

    public Contenido obtener(Long id) {
        return contenidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Contenido", id));
    }

    @Transactional
    public Contenido crear(Contenido datos) {
        Validaciones.requerido(datos, "contenido");

        Unidad unidad = resolverUnidad(datos.getUnidad());
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
        contenidoRepository.delete(obtener(id));
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
}
