package com.example.mpaz_pro_6springboot.curriculo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mpaz_pro_6springboot.common.exception.ConflictoException;
import com.example.mpaz_pro_6springboot.common.exception.RecursoNoEncontradoException;
import com.example.mpaz_pro_6springboot.common.util.Validaciones;
import com.example.mpaz_pro_6springboot.curriculo.model.EstadoAutorizacion;
import com.example.mpaz_pro_6springboot.curriculo.repository.EstadoAutorizacionRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio del catalogo {@link EstadoAutorizacion}.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EstadoAutorizacionService {

    private final EstadoAutorizacionRepository estadoAutorizacionRepository;

    public List<EstadoAutorizacion> listar() {
        return estadoAutorizacionRepository.findAllByOrderByNombreAsc();
    }

    public EstadoAutorizacion obtener(Long id) {
        return estadoAutorizacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("EstadoAutorizacion", id));
    }

    public EstadoAutorizacion obtenerPorNombre(String nombre) {
        String limpio = Validaciones.textoObligatorio(nombre, "nombre");
        return estadoAutorizacionRepository.findByNombre(limpio)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "EstadoAutorizacion no encontrado con nombre " + limpio));
    }

    @Transactional
    public EstadoAutorizacion crear(String nombre) {
        String limpio = Validaciones.textoObligatorio(nombre, "nombre");
        if (estadoAutorizacionRepository.existsByNombre(limpio)) {
            throw new ConflictoException("Ya existe un estado de autorizacion con nombre " + limpio);
        }
        return estadoAutorizacionRepository.save(
                EstadoAutorizacion.builder().nombre(limpio).build());
    }

    @Transactional
    public EstadoAutorizacion actualizar(Long id, String nombre) {
        EstadoAutorizacion estado = obtener(id);
        if (nombre != null) {
            String limpio = Validaciones.textoObligatorio(nombre, "nombre");
            if (!limpio.equals(estado.getNombre())
                    && estadoAutorizacionRepository.existsByNombre(limpio)) {
                throw new ConflictoException(
                        "Ya existe un estado de autorizacion con nombre " + limpio);
            }
            estado.setNombre(limpio);
        }
        return estado;
    }

    @Transactional
    public void eliminar(Long id) {
        estadoAutorizacionRepository.delete(obtener(id));
    }
}
