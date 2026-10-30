package com.example.mpaz_pro_6springboot.identidad.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mpaz_pro_6springboot.common.exception.ConflictoException;
import com.example.mpaz_pro_6springboot.common.exception.RecursoNoEncontradoException;
import com.example.mpaz_pro_6springboot.common.exception.ReglaNegocioException;
import com.example.mpaz_pro_6springboot.common.util.Validaciones;
import com.example.mpaz_pro_6springboot.identidad.model.Seccion;
import com.example.mpaz_pro_6springboot.identidad.repository.SeccionRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio del catalogo {@link Seccion}.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeccionService {

    private final SeccionRepository seccionRepository;

    public List<Seccion> listar() {
        return seccionRepository.findAllByOrderByLetraAsc();
    }

    public Seccion obtener(Long id) {
        return seccionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Seccion", id));
    }

    public Seccion obtenerPorLetra(String letra) {
        String limpia = normalizarLetra(letra);
        return seccionRepository.findByLetra(limpia)
                .orElseThrow(() -> new RecursoNoEncontradoException("Seccion no encontrada con letra " + limpia));
    }

    @Transactional
    public Seccion crear(String letra) {
        String limpia = normalizarLetra(letra);
        if (seccionRepository.existsByLetra(limpia)) {
            throw new ConflictoException("Ya existe una seccion con letra " + limpia);
        }
        return seccionRepository.save(Seccion.builder().letra(limpia).build());
    }

    @Transactional
    public Seccion actualizar(Long id, String letra) {
        Seccion seccion = obtener(id);
        if (letra != null) {
            String limpia = normalizarLetra(letra);
            if (!limpia.equals(seccion.getLetra()) && seccionRepository.existsByLetra(limpia)) {
                throw new ConflictoException("Ya existe una seccion con letra " + limpia);
            }
            seccion.setLetra(limpia);
        }
        return seccion;
    }

    @Transactional
    public void eliminar(Long id) {
        seccionRepository.delete(obtener(id));
    }

    private String normalizarLetra(String letra) {
        String limpia = Validaciones.textoObligatorio(letra, "letra");
        if (limpia.length() != 1) {
            throw new ReglaNegocioException("La letra de la seccion debe tener exactamente 1 caracter");
        }
        return limpia;
    }
}
