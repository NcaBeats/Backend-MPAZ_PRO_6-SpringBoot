package com.example.mpaz_pro_6springboot.curriculo.recurso_contenido.service;

import com.example.mpaz_pro_6springboot.curriculo.contenido.model.Contenido;
import com.example.mpaz_pro_6springboot.curriculo.contenido.repository.ContenidoRepository;
import com.example.mpaz_pro_6springboot.curriculo.recurso_contenido.model.RecursoContenido;
import com.example.mpaz_pro_6springboot.curriculo.recurso_contenido.repository.RecursoContenidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecursoContenidoService {

    private final RecursoContenidoRepository recursoContenidoRepository;
    private final ContenidoRepository contenidoRepository;

    public List<RecursoContenido> findAll(Long contenidoId) {
        List<RecursoContenido> recursos;
        if (contenidoId == null) {
            recursos = recursoContenidoRepository.findAll();
        } else {
            getContenido(contenidoId);
            recursos = recursoContenidoRepository.findByContenidoId(contenidoId);
        }

        return recursos;
    }

    public RecursoContenido findById(Long id) {
        return getRecursoContenido(id);
    }

    @Transactional
    public RecursoContenido create(RecursoContenido recursoContenido, Long contenidoId) {
        Contenido contenido = getContenido(contenidoId);
        recursoContenido.setContenido(contenido);
        return recursoContenidoRepository.save(recursoContenido);
    }

    @Transactional
    public RecursoContenido update(Long id, RecursoContenido cambios) {
        RecursoContenido recursoContenido = getRecursoContenido(id);
        if (cambios.getTipo() != null) {
            recursoContenido.setTipo(cambios.getTipo());
        }
        if (cambios.getUrl() != null) {
            recursoContenido.setUrl(cambios.getUrl());
        }
        if (cambios.getDescripcion() != null) {
            recursoContenido.setDescripcion(cambios.getDescripcion());
        }
        if (cambios.getOrigen() != null) {
            recursoContenido.setOrigen(cambios.getOrigen());
        }
        return recursoContenidoRepository.save(recursoContenido);
    }

    @Transactional
    public void delete(Long id) {
        recursoContenidoRepository.delete(getRecursoContenido(id));
    }

    private RecursoContenido getRecursoContenido(Long id) {
        return recursoContenidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Recurso de contenido no encontrado"
                ));
    }

    private Contenido getContenido(Long id) {
        return contenidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contenido no encontrado"));
    }
}
