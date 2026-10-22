package com.example.mpaz_pro_6springboot.curriculo.asignatura.service;

import com.example.mpaz_pro_6springboot.curriculo.asignatura.model.Asignatura;
import com.example.mpaz_pro_6springboot.curriculo.asignatura.repository.AsignaturaRepository;
import com.example.mpaz_pro_6springboot.curriculo.unidad.repository.UnidadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AsignaturaService {

    private final AsignaturaRepository asignaturaRepository;
    private final UnidadRepository unidadRepository;

    public List<Asignatura> findAll() {
        return asignaturaRepository.findAllByOrderByNombreAsc();
    }

    public Asignatura findById(Long id) {
        return getAsignatura(id);
    }

    @Transactional
    public Asignatura create(Asignatura asignatura) {
        if (asignaturaRepository.existsByNombre(asignatura.getNombre())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una asignatura con ese nombre");
        }

        return asignaturaRepository.save(asignatura);
    }

    @Transactional
    public Asignatura update(Long id, Asignatura cambios) {
        Asignatura asignatura = getAsignatura(id);
        if (asignaturaRepository.existsByNombreAndIdNot(cambios.getNombre(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una asignatura con ese nombre");
        }

        asignatura.setNombre(cambios.getNombre());
        return asignaturaRepository.save(asignatura);
    }

    @Transactional
    public void delete(Long id) {
        Asignatura asignatura = getAsignatura(id);
        if (unidadRepository.existsByAsignaturaId(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar una asignatura que tiene unidades asociadas"
            );
        }

        asignaturaRepository.delete(asignatura);
    }

    private Asignatura getAsignatura(Long id) {
        return asignaturaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asignatura no encontrada"));
    }
}
