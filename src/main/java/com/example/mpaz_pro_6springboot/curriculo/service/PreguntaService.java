package com.example.mpaz_pro_6springboot.curriculo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mpaz_pro_6springboot.common.exception.RecursoNoEncontradoException;
import com.example.mpaz_pro_6springboot.common.exception.ReglaNegocioException;
import com.example.mpaz_pro_6springboot.common.util.Validaciones;
import com.example.mpaz_pro_6springboot.curriculo.model.Pregunta;
import com.example.mpaz_pro_6springboot.curriculo.model.Recurso;
import com.example.mpaz_pro_6springboot.curriculo.repository.PreguntaRepository;
import com.example.mpaz_pro_6springboot.curriculo.repository.RecursoRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de {@link Pregunta}.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PreguntaService {

    private final PreguntaRepository preguntaRepository;
    private final RecursoRepository recursoRepository;

    public List<Pregunta> listar() {
        return preguntaRepository.findAll();
    }

    public List<Pregunta> listarPorRecurso(Long recursoId) {
        return preguntaRepository.findAllByRecursoId(recursoId);
    }

    public List<Pregunta> listarPorContenido(Long contenidoId) {
        return preguntaRepository.findAllByRecursoContenidoId(contenidoId);
    }

    public Pregunta obtener(Long id) {
        return preguntaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pregunta", id));
    }

    @Transactional
    public Pregunta crear(Pregunta datos) {
        Validaciones.requerido(datos, "pregunta");

        Recurso recurso = resolverRecurso(datos.getRecurso());

        datos.setId(null);
        datos.setEnunciado(Validaciones.textoObligatorio(datos.getEnunciado(), "enunciado"));
        datos.setRecurso(recurso);
        return preguntaRepository.save(datos);
    }

    @Transactional
    public Pregunta actualizar(Long id, Pregunta datos) {
        Validaciones.requerido(datos, "pregunta");
        Pregunta pregunta = obtener(id);

        if (datos.getEnunciado() != null) {
            pregunta.setEnunciado(
                    Validaciones.textoObligatorio(datos.getEnunciado(), "enunciado"));
        }
        if (datos.getRecurso() != null) {
            pregunta.setRecurso(resolverRecurso(datos.getRecurso()));
        }
        return pregunta;
    }

    @Transactional
    public void eliminar(Long id) {
        preguntaRepository.delete(obtener(id));
    }

    private Recurso resolverRecurso(Recurso recurso) {
        if (recurso == null || recurso.getId() == null) {
            throw new ReglaNegocioException("El campo recurso es obligatorio");
        }
        return recursoRepository.findById(recurso.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Recurso", recurso.getId()));
    }
}
