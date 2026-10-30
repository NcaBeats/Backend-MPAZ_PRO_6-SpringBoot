package com.example.mpaz_pro_6springboot.identidad.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mpaz_pro_6springboot.common.exception.ConflictoException;
import com.example.mpaz_pro_6springboot.common.exception.RecursoNoEncontradoException;
import com.example.mpaz_pro_6springboot.common.util.Validaciones;
import com.example.mpaz_pro_6springboot.identidad.model.Rol;
import com.example.mpaz_pro_6springboot.identidad.repository.RolRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio del catalogo {@link Rol}.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RolService {

    private final RolRepository rolRepository;

    public List<Rol> listar() {
        return rolRepository.findAllByOrderByNombreAsc();
    }

    public Rol obtener(Long id) {
        return rolRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol", id));
    }

    public Rol obtenerPorNombre(String nombre) {
        String limpio = Validaciones.textoObligatorio(nombre, "nombre");
        return rolRepository.findByNombre(limpio)
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol no encontrado con nombre " + limpio));
    }

    @Transactional
    public Rol crear(String nombre) {
        String limpio = Validaciones.textoObligatorio(nombre, "nombre");
        if (rolRepository.existsByNombre(limpio)) {
            throw new ConflictoException("Ya existe un rol con nombre " + limpio);
        }
        return rolRepository.save(Rol.builder().nombre(limpio).build());
    }

    @Transactional
    public Rol actualizar(Long id, String nombre) {
        Rol rol = obtener(id);
        if (nombre != null) {
            String limpio = Validaciones.textoObligatorio(nombre, "nombre");
            if (!limpio.equals(rol.getNombre()) && rolRepository.existsByNombre(limpio)) {
                throw new ConflictoException("Ya existe un rol con nombre " + limpio);
            }
            rol.setNombre(limpio);
        }
        return rol;
    }

    @Transactional
    public void eliminar(Long id) {
        rolRepository.delete(obtener(id));
    }
}
