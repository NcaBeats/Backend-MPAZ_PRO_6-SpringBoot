package com.example.mpaz_pro_6springboot.curriculo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mpaz_pro_6springboot.common.exception.RecursoNoEncontradoException;
import com.example.mpaz_pro_6springboot.common.exception.ReglaNegocioException;
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

    private final UnidadRepository unidadRepository;
    private final AsignaturaRepository asignaturaRepository;
    private final EstadoAutorizacionRepository estadoAutorizacionRepository;

    public List<Unidad> listar() {
        return unidadRepository.findAll();
    }

    public List<Unidad> listarPorAsignatura(Long asignaturaId) {
        return unidadRepository.findAllByAsignaturaId(asignaturaId);
    }

    public List<Unidad> listarPorEstado(Long estadoId) {
        return unidadRepository.findAllByEstadoId(estadoId);
    }

    public List<Unidad> listarPorAsignaturaYEstado(Long asignaturaId, Long estadoId) {
        return unidadRepository.findAllByAsignaturaIdAndEstadoId(asignaturaId, estadoId);
    }

    public Unidad obtener(Long id) {
        return unidadRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Unidad", id));
    }

    @Transactional
    public Unidad crear(Unidad datos) {
        Validaciones.requerido(datos, "unidad");

        String nombre = Validaciones.textoObligatorio(datos.getNombre(), "nombre");
        Asignatura asignatura = resolverAsignatura(datos.getAsignatura());
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

        if (datos.getNombre() != null) {
            unidad.setNombre(Validaciones.textoObligatorio(datos.getNombre(), "nombre"));
        }
        if (datos.getDescripcion() != null) {
            unidad.setDescripcion(Validaciones.textoOpcional(datos.getDescripcion()));
        }
        if (datos.getAsignatura() != null) {
            unidad.setAsignatura(resolverAsignatura(datos.getAsignatura()));
        }
        if (datos.getEstado() != null) {
            unidad.setEstado(resolverEstado(datos.getEstado()));
        }
        return unidad;
    }

    @Transactional
    public void eliminar(Long id) {
        unidadRepository.delete(obtener(id));
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
}
