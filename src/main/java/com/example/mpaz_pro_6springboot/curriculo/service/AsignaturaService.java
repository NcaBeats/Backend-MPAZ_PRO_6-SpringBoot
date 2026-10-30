package com.example.mpaz_pro_6springboot.curriculo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mpaz_pro_6springboot.common.exception.ConflictoException;
import com.example.mpaz_pro_6springboot.common.exception.RecursoNoEncontradoException;
import com.example.mpaz_pro_6springboot.common.util.Validaciones;
import com.example.mpaz_pro_6springboot.curriculo.model.Asignatura;
import com.example.mpaz_pro_6springboot.curriculo.repository.AsignaturaRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de {@link Asignatura}.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AsignaturaService {

    private final AsignaturaRepository asignaturaRepository;

    public List<Asignatura> listar() {
        return asignaturaRepository.findAllByOrderByNombreAsc();
    }

    public Asignatura obtener(Long id) {
        return asignaturaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Asignatura", id));
    }

    public Asignatura obtenerPorNombre(String nombre) {
        String limpio = Validaciones.textoObligatorio(nombre, "nombre");
        return asignaturaRepository.findByNombre(limpio)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Asignatura no encontrada con nombre " + limpio));
    }

    @Transactional
    public Asignatura crear(String nombre) {
        String limpio = Validaciones.textoObligatorio(nombre, "nombre");
        if (asignaturaRepository.existsByNombre(limpio)) {
            throw new ConflictoException("Ya existe una asignatura con nombre " + limpio);
        }
        return asignaturaRepository.save(Asignatura.builder().nombre(limpio).build());
    }

    @Transactional
    public Asignatura actualizar(Long id, String nombre) {
        Asignatura asignatura = obtener(id);
        if (nombre != null) {
            String limpio = Validaciones.textoObligatorio(nombre, "nombre");
            if (!limpio.equals(asignatura.getNombre())
                    && asignaturaRepository.existsByNombre(limpio)) {
                throw new ConflictoException("Ya existe una asignatura con nombre " + limpio);
            }
            asignatura.setNombre(limpio);
        }
        return asignatura;
    }

    @Transactional
    public void eliminar(Long id) {
        asignaturaRepository.delete(obtener(id));
    }
}
