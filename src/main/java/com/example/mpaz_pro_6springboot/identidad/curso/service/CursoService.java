package com.example.mpaz_pro_6springboot.identidad.curso.service;

import com.example.mpaz_pro_6springboot.common.enums.Rol;
import com.example.mpaz_pro_6springboot.docencia.asignacion.repository.AsignacionRepository;
import com.example.mpaz_pro_6springboot.docencia.docencia.repository.DocenteCursoAsignaturaRepository;
import com.example.mpaz_pro_6springboot.identidad.curso.model.Curso;
import com.example.mpaz_pro_6springboot.identidad.curso.repository.CursoRepository;
import com.example.mpaz_pro_6springboot.identidad.usuario.model.Usuario;
import com.example.mpaz_pro_6springboot.identidad.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CursoService {

    private final CursoRepository cursoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AsignacionRepository asignacionRepository;
    private final DocenteCursoAsignaturaRepository docenciaRepository;

    public List<Curso> findAll(Integer anio) {
        List<Curso> cursos = anio == null
                ? cursoRepository.findAllByOrderByAnioAscNombreAsc()
                : cursoRepository.findByAnioOrderByNombre(anio);
        return cursos;
    }

    public Curso findById(Long id) {
        return getCurso(id);
    }

    /**
     * Lista los estudiantes de un curso.
     *
     * La UTP puede consultar cualquier curso; un docente solo los cursos en los
     * que tiene alguna docencia. El endpoint es de solo lectura y no expone
     * usuarios de otros roles.
     */
    public List<Usuario> findEstudiantes(Long cursoId, Usuario currentUser) {
        getCurso(cursoId);

        if (currentUser.getRol() == Rol.DOCENTE
                && !docenciaRepository.existsByDocenteIdAndCursoId(
                        currentUser.getId(),
                        cursoId
                )) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo puedes consultar estudiantes de tus cursos"
            );
        }

        if (currentUser.getRol() != Rol.DOCENTE && currentUser.getRol() != Rol.UTP) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo la UTP y los docentes pueden consultar estudiantes"
            );
        }

        return usuarioRepository.findByRolAndCursoIdOrderByNombreAsc(
                Rol.ESTUDIANTE,
                cursoId
        );
    }

    @Transactional
    public Curso create(Curso curso) {
        if (cursoRepository.existsByNombreAndAnio(curso.getNombre(), curso.getAnio())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un curso con ese nombre y año");
        }

        return cursoRepository.save(curso);
    }

    @Transactional
    public Curso update(Long id, Curso cambios) {
        if (cambios.getNombre() == null && cambios.getAnio() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe proporcionar al menos un campo para actualizar");
        }

        Curso curso = getCurso(id);
        String nombre = cambios.getNombre() == null ? curso.getNombre() : cambios.getNombre();
        Integer anio = cambios.getAnio() == null ? curso.getAnio() : cambios.getAnio();
        if (cursoRepository.existsByNombreAndAnioAndIdNot(nombre, anio, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un curso con ese nombre y año");
        }

        if (cambios.getNombre() != null) {
            curso.setNombre(cambios.getNombre());
        }
        if (cambios.getAnio() != null) {
            curso.setAnio(cambios.getAnio());
        }
        return cursoRepository.save(curso);
    }

    @Transactional
    public void delete(Long id) {
        Curso curso = getCurso(id);
        if (usuarioRepository.existsByCursoId(id) || asignacionRepository.existsByCursoId(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar un curso que tiene estudiantes o asignaciones asociadas"
            );
        }

        cursoRepository.delete(curso);
    }

    private Curso getCurso(Long id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado"));
    }
}
