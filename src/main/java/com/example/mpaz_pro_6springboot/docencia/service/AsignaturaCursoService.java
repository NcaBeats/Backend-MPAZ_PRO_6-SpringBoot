package com.example.mpaz_pro_6springboot.docencia.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mpaz_pro_6springboot.common.exception.ConflictoException;
import com.example.mpaz_pro_6springboot.common.exception.RecursoNoEncontradoException;
import com.example.mpaz_pro_6springboot.common.exception.ReglaNegocioException;
import com.example.mpaz_pro_6springboot.common.util.Validaciones;
import com.example.mpaz_pro_6springboot.curriculo.model.Asignatura;
import com.example.mpaz_pro_6springboot.curriculo.repository.AsignaturaRepository;
import com.example.mpaz_pro_6springboot.docencia.model.AsignaturaCurso;
import com.example.mpaz_pro_6springboot.docencia.repository.AsignaturaCursoRepository;
import com.example.mpaz_pro_6springboot.identidad.model.Curso;
import com.example.mpaz_pro_6springboot.identidad.repository.CursoRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de la relacion {@link AsignaturaCurso}.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AsignaturaCursoService {

    private final AsignaturaCursoRepository asignaturaCursoRepository;
    private final CursoRepository cursoRepository;
    private final AsignaturaRepository asignaturaRepository;

    public List<AsignaturaCurso> listar() {
        return asignaturaCursoRepository.findAll();
    }

    public List<AsignaturaCurso> listarPorCurso(Long cursoId) {
        return asignaturaCursoRepository.findAllByCursoId(cursoId);
    }

    public List<AsignaturaCurso> listarPorAsignatura(Long asignaturaId) {
        return asignaturaCursoRepository.findAllByAsignaturaId(asignaturaId);
    }

    public AsignaturaCurso obtener(Long id) {
        return asignaturaCursoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("AsignaturaCurso", id));
    }

    public boolean existe(Long cursoId, Long asignaturaId) {
        return asignaturaCursoRepository.existsByCursoIdAndAsignaturaId(cursoId, asignaturaId);
    }

    @Transactional
    public AsignaturaCurso crear(AsignaturaCurso datos) {
        Validaciones.requerido(datos, "asignaturaCurso");

        Curso curso = resolverCurso(datos.getCurso());
        Asignatura asignatura = resolverAsignatura(datos.getAsignatura());

        if (asignaturaCursoRepository.existsByCursoIdAndAsignaturaId(curso.getId(), asignatura.getId())) {
            throw new ConflictoException(
                    "La asignatura ya esta registrada en el curso indicado");
        }
        validarNivel(curso, asignatura);

        datos.setId(null);
        datos.setCurso(curso);
        datos.setAsignatura(asignatura);
        return asignaturaCursoRepository.save(datos);
    }

    @Transactional
    public void eliminar(Long id) {
        asignaturaCursoRepository.delete(obtener(id));
    }

    /**
     * Regla de negocio: un curso solo puede ofrecer asignaturas de su nivel.
     * El esquema no lo expresa porque `curso.nivel_id` y `asignatura.nivel_id`
     * son FKs independientes.
     */
    private void validarNivel(Curso curso, Asignatura asignatura) {
        if (!curso.getNivel().getId().equals(asignatura.getNivel().getId())) {
            throw new ReglaNegocioException(
                    "La asignatura no pertenece al nivel del curso");
        }
    }

    private Curso resolverCurso(Curso curso) {
        if (curso == null || curso.getId() == null) {
            throw new ReglaNegocioException("El campo curso es obligatorio");
        }
        return cursoRepository.findById(curso.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Curso", curso.getId()));
    }

    private Asignatura resolverAsignatura(Asignatura asignatura) {
        if (asignatura == null || asignatura.getId() == null) {
            throw new ReglaNegocioException("El campo asignatura es obligatorio");
        }
        return asignaturaRepository.findById(asignatura.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Asignatura", asignatura.getId()));
    }
}
