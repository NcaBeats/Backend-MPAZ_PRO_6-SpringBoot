package com.example.mpaz_pro_6springboot.curriculo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mpaz_pro_6springboot.common.exception.ConflictoException;
import com.example.mpaz_pro_6springboot.common.exception.RecursoNoEncontradoException;
import com.example.mpaz_pro_6springboot.common.util.Validaciones;
import com.example.mpaz_pro_6springboot.curriculo.model.TipoActividad;
import com.example.mpaz_pro_6springboot.curriculo.repository.TipoActividadRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio del catalogo {@link TipoActividad}.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TipoActividadService {

    private final TipoActividadRepository tipoActividadRepository;

    public List<TipoActividad> listar() {
        return tipoActividadRepository.findAllByOrderByNombreAsc();
    }

    public TipoActividad obtener(Long id) {
        return tipoActividadRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("TipoActividad", id));
    }

    public TipoActividad obtenerPorNombre(String nombre) {
        String limpio = Validaciones.textoObligatorio(nombre, "nombre");
        return tipoActividadRepository.findByNombre(limpio)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "TipoActividad no encontrado con nombre " + limpio));
    }

    @Transactional
    public TipoActividad crear(String nombre) {
        String limpio = Validaciones.textoObligatorio(nombre, "nombre");
        if (tipoActividadRepository.existsByNombre(limpio)) {
            throw new ConflictoException("Ya existe un tipo de actividad con nombre " + limpio);
        }
        return tipoActividadRepository.save(TipoActividad.builder().nombre(limpio).build());
    }

    @Transactional
    public TipoActividad actualizar(Long id, String nombre) {
        TipoActividad tipo = obtener(id);
        if (nombre != null) {
            String limpio = Validaciones.textoObligatorio(nombre, "nombre");
            if (!limpio.equals(tipo.getNombre())
                    && tipoActividadRepository.existsByNombre(limpio)) {
                throw new ConflictoException("Ya existe un tipo de actividad con nombre " + limpio);
            }
            tipo.setNombre(limpio);
        }
        return tipo;
    }

    @Transactional
    public void eliminar(Long id) {
        tipoActividadRepository.delete(obtener(id));
    }
}
