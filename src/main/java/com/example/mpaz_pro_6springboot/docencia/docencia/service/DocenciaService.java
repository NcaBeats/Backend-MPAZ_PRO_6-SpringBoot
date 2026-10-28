package com.example.mpaz_pro_6springboot.docencia.docencia.service;

import com.example.mpaz_pro_6springboot.common.enums.Rol;
import com.example.mpaz_pro_6springboot.curriculo.asignatura.model.Asignatura;
import com.example.mpaz_pro_6springboot.curriculo.asignatura.repository.AsignaturaRepository;
import com.example.mpaz_pro_6springboot.docencia.asignacion.repository.AsignacionRepository;
import com.example.mpaz_pro_6springboot.docencia.docencia.dto.request.DocenciaCreateRequest;
import com.example.mpaz_pro_6springboot.docencia.docencia.dto.request.DocenciaUpdateRequest;
import com.example.mpaz_pro_6springboot.docencia.docencia.model.DocenteCursoAsignatura;
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
public class DocenciaService {

    private final DocenteCursoAsignaturaRepository docenciaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CursoRepository cursoRepository;
    private final AsignaturaRepository asignaturaRepository;
    private final AsignacionRepository asignacionRepository;

    /**
     * UTP ve todas las docencias. Un docente solo ve las suyas.
     */
    public List<DocenteCursoAsignatura> findAll(Long docenteId, Usuario currentUser) {
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
        if (currentUser.getRol() == Rol.UTP) {
            if (docenteId == null) {
                return docenciaRepository.findAllByOrderByIdAsc();
            }
            getDocente(docenteId);
            return docenciaRepository.findByDocenteIdOrderByIdAsc(docenteId);
        }
        if (currentUser.getRol() != Rol.DOCENTE) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo docentes y UTP pueden consultar docencias"
            );
        }
        if (docenteId != null && !docenteId.equals(currentUser.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Un docente solo puede consultar sus propias docencias"
            );
        }
        return docenciaRepository.findByDocenteIdOrderByIdAsc(currentUser.getId());
    }

    public DocenteCursoAsignatura findById(Long id, Usuario currentUser) {
        DocenteCursoAsignatura docencia = getDocencia(id);
        assertPuedeLeer(docencia, currentUser);
        return docencia;
    }

    @Transactional
    public DocenteCursoAsignatura create(DocenciaCreateRequest request) {
        Usuario docente = getDocente(request.docenteId());
        Curso curso = getCurso(request.cursoId());
        Asignatura asignatura = getAsignatura(request.asignaturaId());

        if (docenciaRepository.existsByCursoIdAndAsignaturaId(
                curso.getId(),
                asignatura.getId()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un docente para ese curso y asignatura"
            );
        }

        return docenciaRepository.save(DocenteCursoAsignatura.builder()
                .docente(docente)
                .curso(curso)
                .asignatura(asignatura)
                .build());
    }

    @Transactional
    public DocenteCursoAsignatura update(Long id, DocenciaUpdateRequest request) {
        DocenteCursoAsignatura docencia = getDocencia(id);
        Usuario docente = getDocente(request.docenteId());
        docencia.setDocente(docente);
        return docenciaRepository.save(docencia);
    }

    @Transactional
    public void delete(Long id) {
        DocenteCursoAsignatura docencia = getDocencia(id);
        if (asignacionRepository.existsByCursoIdAndUnidadAsignaturaId(
                docencia.getCurso().getId(),
                docencia.getAsignatura().getId()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar una docencia con asignaciones asociadas"
            );
        }
        docenciaRepository.delete(docencia);
    }

    /** Cursos distintos del docente, para el endpoint historico. */
    public List<Curso> findCursosByDocente(Long docenteId) {
        getDocente(docenteId);
        return docenciaRepository.findByDocenteIdOrderByIdAsc(docenteId).stream()
                .map(DocenteCursoAsignatura::getCurso)
                .distinct()
                .toList();
    }

    private void assertPuedeLeer(DocenteCursoAsignatura docencia, Usuario currentUser) {
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
        if (currentUser.getRol() == Rol.UTP) {
            return;
        }
        if (currentUser.getRol() != Rol.DOCENTE
                || !docencia.getDocente().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No puedes consultar una docencia ajena"
            );
        }
    }

    private DocenteCursoAsignatura getDocencia(Long id) {
        return docenciaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Docencia no encontrada"
                ));
    }

    private Usuario getDocente(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Docente no encontrado"
                ));
        if (usuario.getRol() != Rol.DOCENTE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El usuario debe tener rol DOCENTE"
            );
        }
        return usuario;
    }

    private Curso getCurso(Long id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Curso no encontrado"
                ));
    }

    private Asignatura getAsignatura(Long id) {
        return asignaturaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Asignatura no encontrada"
                ));
    }
}
