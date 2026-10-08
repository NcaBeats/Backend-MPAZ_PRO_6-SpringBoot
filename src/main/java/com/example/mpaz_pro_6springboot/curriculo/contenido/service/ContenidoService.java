package com.example.mpaz_pro_6springboot.curriculo.contenido.service;

import com.example.mpaz_pro_6springboot.curriculo.contenido.model.Contenido;
import com.example.mpaz_pro_6springboot.curriculo.contenido.repository.ContenidoRepository;
import com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.model.ObjetivoAprendizaje;
import com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.repository.ObjetivoAprendizajeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContenidoService {

    private static final int ORDEN_PREDETERMINADO = 1;

    private final ContenidoRepository contenidoRepository;
    private final ObjetivoAprendizajeRepository objetivoAprendizajeRepository;

    public List<Contenido> findAll(Long oaId) {
        if (oaId == null) {
            return contenidoRepository.findAllByOrderByOaIdAscOrdenAscIdAsc();
        }

        getObjetivoAprendizaje(oaId);
        return contenidoRepository.findByOaIdOrderByOrdenAscIdAsc(oaId);
    }

    public Contenido findById(Long id) {
        return getContenido(id);
    }

    @Transactional
    public Contenido create(Contenido contenido, Long oaId) {
        contenido.setOa(getObjetivoAprendizaje(oaId));
        if (contenido.getOrden() == null) {
            contenido.setOrden(ORDEN_PREDETERMINADO);
        }
        return contenidoRepository.save(contenido);
    }

    @Transactional
    public Contenido update(Long id, Contenido cambios) {
        if (isEmptyUpdate(cambios)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe proporcionar al menos un campo para actualizar"
            );
        }

        Contenido contenido = getContenido(id);
        if (cambios.getTitulo() != null) {
            contenido.setTitulo(cambios.getTitulo());
        }
        if (cambios.getExplicacion() != null) {
            contenido.setExplicacion(cambios.getExplicacion());
        }
        if (cambios.getEjemplos() != null) {
            contenido.setEjemplos(cambios.getEjemplos());
        }
        if (cambios.getInstrucciones() != null) {
            contenido.setInstrucciones(cambios.getInstrucciones());
        }
        if (cambios.getOrden() != null) {
            contenido.setOrden(cambios.getOrden());
        }
        if (cambios.getOrigen() != null) {
            contenido.setOrigen(cambios.getOrigen());
        }
        return contenidoRepository.save(contenido);
    }

    @Transactional
    public void delete(Long id) {
        contenidoRepository.delete(getContenido(id));
    }

    private boolean isEmptyUpdate(Contenido cambios) {
        return cambios.getTitulo() == null
                && cambios.getExplicacion() == null
                && cambios.getEjemplos() == null
                && cambios.getInstrucciones() == null
                && cambios.getOrden() == null
                && cambios.getOrigen() == null;
    }

    private Contenido getContenido(Long id) {
        return contenidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contenido no encontrado"));
    }

    private ObjetivoAprendizaje getObjetivoAprendizaje(Long id) {
        return objetivoAprendizajeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Objetivo de aprendizaje no encontrado"
                ));
    }
}
