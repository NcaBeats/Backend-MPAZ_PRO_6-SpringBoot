package com.example.mpaz_pro_6springboot.curriculo.recurso_contenido.service;

import com.example.mpaz_pro_6springboot.common.enums.EstadoContenido;
import com.example.mpaz_pro_6springboot.common.security.StudentVisibilityService;
import com.example.mpaz_pro_6springboot.curriculo.contenido.model.Contenido;
import com.example.mpaz_pro_6springboot.curriculo.contenido.repository.ContenidoRepository;
import com.example.mpaz_pro_6springboot.curriculo.recurso_contenido.model.RecursoContenido;
import com.example.mpaz_pro_6springboot.curriculo.recurso_contenido.repository.RecursoContenidoRepository;
import com.example.mpaz_pro_6springboot.identidad.usuario.model.Usuario;
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
    private final StudentVisibilityService visibilityService;

    public List<RecursoContenido> findAll(Long contenidoId, Usuario currentUser) {
        List<RecursoContenido> recursos;
        if (contenidoId == null) {
            recursos = recursoContenidoRepository.findAll();
        } else {
            Contenido contenido = getContenido(contenidoId);
            if (visibilityService.isStudent(currentUser)
                    && contenido.getEstado() != EstadoContenido.PUBLICADO) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "El contenido no está publicado"
                );
            }
            if (!visibilityService.canSeeObjective(currentUser, contenido.getOa().getId())) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "El contenido no está asociado a una asignación del estudiante"
                );
            }
            recursos = recursoContenidoRepository.findByContenidoId(contenidoId);
        }

        if (!visibilityService.isStudent(currentUser)) {
            return recursos;
        }

        return recursos.stream()
                .filter(recurso -> recurso.getEstado() == EstadoContenido.PUBLICADO)
                .filter(recurso -> recurso.getContenido().getEstado() == EstadoContenido.PUBLICADO)
                .filter(recurso -> visibilityService.canSeeObjective(
                        currentUser,
                        recurso.getContenido().getOa().getId()
                ))
                .toList();
    }

    public RecursoContenido findById(Long id, Usuario currentUser) {
        RecursoContenido recurso = getRecursoContenido(id);
        if (visibilityService.isStudent(currentUser)
                && (recurso.getEstado() != EstadoContenido.PUBLICADO
                || recurso.getContenido().getEstado() != EstadoContenido.PUBLICADO)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Recurso de contenido no encontrado"
            );
        }
        if (!visibilityService.canSeeObjective(
                currentUser,
                recurso.getContenido().getOa().getId()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "El recurso no está asociado a una asignación del estudiante"
            );
        }
        return recurso;
    }

    @Transactional
    public RecursoContenido create(RecursoContenido recursoContenido, Long contenidoId) {
        Contenido contenido = getContenido(contenidoId);
        recursoContenido.setContenido(contenido);
        recursoContenido.setEstado(contenido.getEstado());
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
