package com.example.mpaz_pro_6springboot.identidad.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mpaz_pro_6springboot.common.exception.ConflictoException;
import com.example.mpaz_pro_6springboot.common.exception.RecursoNoEncontradoException;
import com.example.mpaz_pro_6springboot.common.exception.ReglaNegocioException;
import com.example.mpaz_pro_6springboot.common.util.Validaciones;
import com.example.mpaz_pro_6springboot.identidad.model.Curso;
import com.example.mpaz_pro_6springboot.identidad.model.Nivel;
import com.example.mpaz_pro_6springboot.identidad.model.Seccion;
import com.example.mpaz_pro_6springboot.identidad.repository.CursoRepository;
import com.example.mpaz_pro_6springboot.identidad.repository.NivelRepository;
import com.example.mpaz_pro_6springboot.identidad.repository.SeccionRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de {@link Curso}.
 *
 * <p>La combinacion {@code (annio, nivel, seccion)} es unica en la base de
 * datos; el servicio la valida antes de guardar para entregar un error claro.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CursoService {

    private final CursoRepository cursoRepository;
    private final NivelRepository nivelRepository;
    private final SeccionRepository seccionRepository;

    public List<Curso> listar() {
        return cursoRepository.findAll();
    }

    public List<Curso> listarPorAnnio(Integer annio) {
        return cursoRepository.findAllByAnnio(annio);
    }

    public List<Curso> listarPorNivel(Long nivelId) {
        return cursoRepository.findAllByNivelId(nivelId);
    }

    public List<Curso> listarPorSeccion(Long seccionId) {
        return cursoRepository.findAllBySeccionId(seccionId);
    }

    public Curso obtener(Long id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Curso", id));
    }

    public Curso obtenerPorCombinacion(Integer annio, Long nivelId, Long seccionId) {
        return cursoRepository.findByAnnioAndNivelIdAndSeccionId(annio, nivelId, seccionId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Curso no encontrado para annio " + annio + ", nivel " + nivelId
                                + " y seccion " + seccionId));
    }

    @Transactional
    public Curso crear(Curso datos) {
        Validaciones.requerido(datos, "curso");

        Integer annio = validarAnnio(datos.getAnnio());
        Nivel nivel = resolverNivel(datos.getNivel());
        Seccion seccion = resolverSeccion(datos.getSeccion());

        if (cursoRepository.existsByAnnioAndNivelIdAndSeccionId(annio, nivel.getId(), seccion.getId())) {
            throw new ConflictoException(
                    "Ya existe un curso para el annio, nivel y seccion indicados");
        }

        datos.setId(null);
        datos.setAnnio(annio);
        datos.setNivel(nivel);
        datos.setSeccion(seccion);
        return cursoRepository.save(datos);
    }

    @Transactional
    public Curso actualizar(Long id, Curso datos) {
        Validaciones.requerido(datos, "curso");
        Curso curso = obtener(id);

        Integer annio = datos.getAnnio() != null ? validarAnnio(datos.getAnnio()) : curso.getAnnio();
        Nivel nivel = datos.getNivel() != null ? resolverNivel(datos.getNivel()) : curso.getNivel();
        Seccion seccion = datos.getSeccion() != null ? resolverSeccion(datos.getSeccion()) : curso.getSeccion();

        cursoRepository.findByAnnioAndNivelIdAndSeccionId(annio, nivel.getId(), seccion.getId())
                .filter(otro -> !otro.getId().equals(id))
                .ifPresent(otro -> {
                    throw new ConflictoException(
                            "Ya existe un curso para el annio, nivel y seccion indicados");
                });

        curso.setAnnio(annio);
        curso.setNivel(nivel);
        curso.setSeccion(seccion);
        return curso;
    }

    @Transactional
    public void eliminar(Long id) {
        cursoRepository.delete(obtener(id));
    }

    private Integer validarAnnio(Integer annio) {
        if (annio == null || annio <= 0) {
            throw new ReglaNegocioException("El annio debe ser mayor que cero");
        }
        return annio;
    }

    private Nivel resolverNivel(Nivel nivel) {
        if (nivel == null || nivel.getId() == null) {
            throw new ReglaNegocioException("El campo nivel es obligatorio");
        }
        return nivelRepository.findById(nivel.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Nivel", nivel.getId()));
    }

    private Seccion resolverSeccion(Seccion seccion) {
        if (seccion == null || seccion.getId() == null) {
            throw new ReglaNegocioException("El campo seccion es obligatorio");
        }
        return seccionRepository.findById(seccion.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Seccion", seccion.getId()));
    }
}
