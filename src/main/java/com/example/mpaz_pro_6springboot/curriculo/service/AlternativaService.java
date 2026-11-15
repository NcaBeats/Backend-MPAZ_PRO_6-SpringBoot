package com.example.mpaz_pro_6springboot.curriculo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mpaz_pro_6springboot.common.exception.RecursoNoEncontradoException;
import com.example.mpaz_pro_6springboot.common.exception.ReglaNegocioException;
import com.example.mpaz_pro_6springboot.common.util.Validaciones;
import com.example.mpaz_pro_6springboot.curriculo.model.Alternativa;
import com.example.mpaz_pro_6springboot.curriculo.model.Recurso;
import com.example.mpaz_pro_6springboot.curriculo.repository.AlternativaRepository;
import com.example.mpaz_pro_6springboot.curriculo.repository.RecursoRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de {@link Alternativa}.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlternativaService {

    private final AlternativaRepository alternativaRepository;
    private final RecursoRepository recursoRepository;

    public List<Alternativa> listar() {
        return alternativaRepository.findAll();
    }

    public List<Alternativa> listarPorRecurso(Long recursoId) {
        return alternativaRepository.findAllByRecursoId(recursoId);
    }

    public List<Alternativa> listarCorrectasPorRecurso(Long recursoId) {
        return alternativaRepository.findAllByRecursoIdAndCorrectaTrue(recursoId);
    }

    public Alternativa obtener(Long id) {
        return alternativaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Alternativa", id));
    }

    @Transactional
    public Alternativa crear(Alternativa datos) {
        Validaciones.requerido(datos, "alternativa");

        Recurso recurso = resolverRecurso(datos.getRecurso());

        datos.setId(null);
        datos.setEnunciado(Validaciones.textoOpcional(datos.getEnunciado()));
        datos.setRecurso(recurso);
        return alternativaRepository.save(datos);
    }

    /**
     * Actualiza los datos editables. El flag {@code correcta} se cambia con
     * {@link #cambiarCorrecta(Long, boolean)}.
     */
    @Transactional
    public Alternativa actualizar(Long id, Alternativa datos) {
        Validaciones.requerido(datos, "alternativa");
        Alternativa alternativa = obtener(id);

        if (datos.getEnunciado() != null) {
            alternativa.setEnunciado(Validaciones.textoOpcional(datos.getEnunciado()));
        }
        if (datos.getRecurso() != null) {
            alternativa.setRecurso(resolverRecurso(datos.getRecurso()));
        }
        return alternativa;
    }

    @Transactional
    public Alternativa cambiarCorrecta(Long id, boolean correcta) {
        Alternativa alternativa = obtener(id);
        alternativa.setCorrecta(correcta);
        return alternativa;
    }

    @Transactional
    public void eliminar(Long id) {
        alternativaRepository.delete(obtener(id));
    }

    private Recurso resolverRecurso(Recurso recurso) {
        if (recurso == null || recurso.getId() == null) {
            throw new ReglaNegocioException("El campo recurso es obligatorio");
        }
        return recursoRepository.findById(recurso.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Recurso", recurso.getId()));
    }
}
