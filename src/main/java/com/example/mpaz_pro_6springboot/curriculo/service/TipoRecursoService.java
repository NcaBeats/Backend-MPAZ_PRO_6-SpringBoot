package com.example.mpaz_pro_6springboot.curriculo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mpaz_pro_6springboot.common.exception.ConflictoException;
import com.example.mpaz_pro_6springboot.common.exception.RecursoNoEncontradoException;
import com.example.mpaz_pro_6springboot.common.util.Validaciones;
import com.example.mpaz_pro_6springboot.curriculo.model.TipoRecurso;
import com.example.mpaz_pro_6springboot.curriculo.repository.TipoRecursoRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio del catalogo {@link TipoRecurso}.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TipoRecursoService {

    private final TipoRecursoRepository tipoRecursoRepository;

    public List<TipoRecurso> listar() {
        return tipoRecursoRepository.findAllByOrderByNombreAsc();
    }

    public TipoRecurso obtener(Long id) {
        return tipoRecursoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("TipoRecurso", id));
    }

    public TipoRecurso obtenerPorNombre(String nombre) {
        String limpio = Validaciones.textoObligatorio(nombre, "nombre");
        return tipoRecursoRepository.findByNombre(limpio)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "TipoRecurso no encontrado con nombre " + limpio));
    }

    @Transactional
    public TipoRecurso crear(String nombre) {
        String limpio = Validaciones.textoObligatorio(nombre, "nombre");
        if (tipoRecursoRepository.existsByNombre(limpio)) {
            throw new ConflictoException("Ya existe un tipo de recurso con nombre " + limpio);
        }
        return tipoRecursoRepository.save(TipoRecurso.builder().nombre(limpio).build());
    }

    @Transactional
    public TipoRecurso actualizar(Long id, String nombre) {
        TipoRecurso tipo = obtener(id);
        if (nombre != null) {
            String limpio = Validaciones.textoObligatorio(nombre, "nombre");
            if (!limpio.equals(tipo.getNombre())
                    && tipoRecursoRepository.existsByNombre(limpio)) {
                throw new ConflictoException("Ya existe un tipo de recurso con nombre " + limpio);
            }
            tipo.setNombre(limpio);
        }
        return tipo;
    }

    @Transactional
    public void eliminar(Long id) {
        tipoRecursoRepository.delete(obtener(id));
    }
}
