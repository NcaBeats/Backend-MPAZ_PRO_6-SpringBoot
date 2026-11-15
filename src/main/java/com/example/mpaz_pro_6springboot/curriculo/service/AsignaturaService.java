package com.example.mpaz_pro_6springboot.curriculo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mpaz_pro_6springboot.common.exception.ConflictoException;
import com.example.mpaz_pro_6springboot.common.exception.RecursoNoEncontradoException;
import com.example.mpaz_pro_6springboot.common.exception.ReglaNegocioException;
import com.example.mpaz_pro_6springboot.common.util.Validaciones;
import com.example.mpaz_pro_6springboot.curriculo.model.Asignatura;
import com.example.mpaz_pro_6springboot.curriculo.repository.AsignaturaRepository;
import com.example.mpaz_pro_6springboot.identidad.model.Nivel;
import com.example.mpaz_pro_6springboot.identidad.repository.NivelRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de {@link Asignatura}.
 *
 * <p>El nombre es unico dentro de un nivel: "Matematica" puede existir una vez
 * por nivel. La unidad hereda el nivel a traves de su asignatura.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AsignaturaService {

    private final AsignaturaRepository asignaturaRepository;
    private final NivelRepository nivelRepository;

    public List<Asignatura> listar() {
        return asignaturaRepository.findAll(Sort.by("nivel.nombre", "nombre"));
    }

    public List<Asignatura> listarPorNivel(Long nivelId) {
        return asignaturaRepository.findAllByNivelIdOrderByNombreAsc(nivelId);
    }

    public Asignatura obtener(Long id) {
        return asignaturaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Asignatura", id));
    }

    @Transactional
    public Asignatura crear(String nombre, Long nivelId) {
        String limpio = Validaciones.textoObligatorio(nombre, "nombre");
        Nivel nivel = resolverNivel(nivelId);
        if (asignaturaRepository.existsByNivelIdAndNombre(nivel.getId(), limpio)) {
            throw new ConflictoException(
                    "Ya existe una asignatura con nombre " + limpio + " en el nivel indicado");
        }
        return asignaturaRepository.save(
                Asignatura.builder().nombre(limpio).nivel(nivel).build());
    }

    @Transactional
    public Asignatura actualizar(Long id, String nombre, Long nivelId) {
        Asignatura asignatura = obtener(id);

        String nombreFinal = asignatura.getNombre();
        Nivel nivelFinal = asignatura.getNivel();
        if (nombre != null) {
            nombreFinal = Validaciones.textoObligatorio(nombre, "nombre");
        }
        if (nivelId != null) {
            nivelFinal = resolverNivel(nivelId);
        }

        Optional<Asignatura> existente = asignaturaRepository
                .findByNivelIdAndNombre(nivelFinal.getId(), nombreFinal);
        if (existente.isPresent() && !existente.get().getId().equals(asignatura.getId())) {
            throw new ConflictoException(
                    "Ya existe una asignatura con nombre " + nombreFinal + " en el nivel indicado");
        }

        asignatura.setNombre(nombreFinal);
        asignatura.setNivel(nivelFinal);
        return asignatura;
    }

    @Transactional
    public void eliminar(Long id) {
        asignaturaRepository.delete(obtener(id));
    }

    private Nivel resolverNivel(Long nivelId) {
        if (nivelId == null) {
            throw new ReglaNegocioException("El campo nivel es obligatorio");
        }
        return nivelRepository.findById(nivelId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Nivel", nivelId));
    }
}
