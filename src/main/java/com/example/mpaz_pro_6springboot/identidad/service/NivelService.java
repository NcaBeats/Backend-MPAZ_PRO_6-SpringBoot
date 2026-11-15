package com.example.mpaz_pro_6springboot.identidad.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mpaz_pro_6springboot.common.exception.ConflictoException;
import com.example.mpaz_pro_6springboot.common.exception.RecursoNoEncontradoException;
import com.example.mpaz_pro_6springboot.common.util.Validaciones;
import com.example.mpaz_pro_6springboot.identidad.model.Nivel;
import com.example.mpaz_pro_6springboot.identidad.repository.NivelRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio del catalogo {@link Nivel}.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NivelService {

    private final NivelRepository nivelRepository;

    public List<Nivel> listar() {
        return nivelRepository.findAllByOrderByNombreAsc();
    }

    public Nivel obtener(Long id) {
        return nivelRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Nivel", id));
    }

    public Nivel obtenerPorNombre(String nombre) {
        String limpio = Validaciones.textoObligatorio(nombre, "nombre");
        return nivelRepository.findByNombre(limpio)
                .orElseThrow(() -> new RecursoNoEncontradoException("Nivel no encontrado con nombre " + limpio));
    }

    @Transactional
    public Nivel crear(String nombre) {
        String limpio = Validaciones.textoObligatorio(nombre, "nombre");
        if (nivelRepository.existsByNombre(limpio)) {
            throw new ConflictoException("Ya existe un nivel con nombre " + limpio);
        }
        return nivelRepository.save(Nivel.builder().nombre(limpio).build());
    }

    @Transactional
    public Nivel actualizar(Long id, String nombre) {
        Nivel nivel = obtener(id);
        if (nombre != null) {
            String limpio = Validaciones.textoObligatorio(nombre, "nombre");
            if (!limpio.equals(nivel.getNombre()) && nivelRepository.existsByNombre(limpio)) {
                throw new ConflictoException("Ya existe un nivel con nombre " + limpio);
            }
            nivel.setNombre(limpio);
        }
        return nivel;
    }

    @Transactional
    public void eliminar(Long id) {
        nivelRepository.delete(obtener(id));
    }
}
