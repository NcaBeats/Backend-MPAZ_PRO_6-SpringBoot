package com.example.mpaz_pro_6springboot.docencia.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.mpaz_pro_6springboot.common.exception.ConflictoException;
import com.example.mpaz_pro_6springboot.common.exception.RecursoNoEncontradoException;
import com.example.mpaz_pro_6springboot.common.exception.ReglaNegocioException;
import com.example.mpaz_pro_6springboot.common.security.SecurityUtils;
import com.example.mpaz_pro_6springboot.common.util.Validaciones;
import com.example.mpaz_pro_6springboot.curriculo.model.Asignatura;
import com.example.mpaz_pro_6springboot.curriculo.repository.AsignaturaRepository;
import com.example.mpaz_pro_6springboot.docencia.model.AsignaturaCurso;
import com.example.mpaz_pro_6springboot.docencia.model.UsuarioCurso;
import com.example.mpaz_pro_6springboot.docencia.repository.AsignaturaCursoRepository;
import com.example.mpaz_pro_6springboot.docencia.repository.UsuarioCursoRepository;
import com.example.mpaz_pro_6springboot.identidad.model.Curso;
import com.example.mpaz_pro_6springboot.identidad.model.Usuario;
import com.example.mpaz_pro_6springboot.identidad.repository.CursoRepository;
import com.example.mpaz_pro_6springboot.identidad.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de la relacion {@link AsignaturaCurso}.
 *
 * <p>Ademas de validar la relacion, mantiene sincronizada la participacion
 * del docente en el curso ({@code usuario_curso}): al asignar un docente se
 * crea la fila y al desasignarlo se elimina si no le quedan otras asignaturas
 * en ese curso.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AsignaturaCursoService {

    private static final String ROL_DOCENTE = "DOCENTE";

    private final AsignaturaCursoRepository asignaturaCursoRepository;
    private final UsuarioCursoRepository usuarioCursoRepository;
    private final CursoRepository cursoRepository;
    private final AsignaturaRepository asignaturaRepository;
    private final UsuarioRepository usuarioRepository;

    public List<AsignaturaCurso> listar() {
        return asignaturaCursoRepository.findAll();
    }

    public List<AsignaturaCurso> listarPorCurso(Long cursoId) {
        return asignaturaCursoRepository.findAllByCursoId(cursoId);
    }

    public List<AsignaturaCurso> listarPorAsignatura(Long asignaturaId) {
        return asignaturaCursoRepository.findAllByAsignaturaId(asignaturaId);
    }

    public List<AsignaturaCurso> listarPorDocente(Long docenteId) {
        if (SecurityUtils.isEstudiante()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Un estudiante no puede consultar docencias");
        }
        if (SecurityUtils.isDocente()
                && !SecurityUtils.requireUsuario().getId().equals(docenteId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Un docente solo puede consultar sus propias docencias");
        }
        return asignaturaCursoRepository.findAllByDocenteId(docenteId);
    }

    public AsignaturaCurso obtener(Long id) {
        return asignaturaCursoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("AsignaturaCurso", id));
    }

    public boolean existe(Long cursoId, Long asignaturaId) {
        return asignaturaCursoRepository.existsByCursoIdAndAsignaturaId(cursoId, asignaturaId);
    }

    @Transactional
    public AsignaturaCurso crear(AsignaturaCurso datos) {
        Validaciones.requerido(datos, "asignaturaCurso");

        Curso curso = resolverCurso(datos.getCurso());
        Asignatura asignatura = resolverAsignatura(datos.getAsignatura());
        Usuario docente = resolverDocente(datos.getDocente());

        if (asignaturaCursoRepository.existsByCursoIdAndAsignaturaId(curso.getId(), asignatura.getId())) {
            throw new ConflictoException(
                    "La asignatura ya esta registrada en el curso indicado");
        }
        validarNivel(curso, asignatura);

        datos.setId(null);
        datos.setCurso(curso);
        datos.setAsignatura(asignatura);
        datos.setDocente(docente);
        AsignaturaCurso guardada = asignaturaCursoRepository.save(datos);
        asegurarUsuarioCurso(docente, curso);
        return guardada;
    }

    /**
     * Asigna, reasigna o desasigna el docente de una relacion ya existente.
     *
     * @param docenteId identificador del docente; {@code null} desasigna.
     */
    @Transactional
    public AsignaturaCurso actualizarDocente(Long id, Long docenteId) {
        AsignaturaCurso relacion = obtener(id);
        Usuario anterior = relacion.getDocente();
        Usuario nuevo = resolverDocentePorId(docenteId);

        if (anterior != null && (nuevo == null || !anterior.getId().equals(nuevo.getId()))) {
            relacion.setDocente(null);
            asignaturaCursoRepository.saveAndFlush(relacion);
            limpiarUsuarioCursoSiCorresponde(anterior, relacion.getCurso());
        }

        relacion.setDocente(nuevo);
        AsignaturaCurso guardada = asignaturaCursoRepository.save(relacion);
        asegurarUsuarioCurso(nuevo, guardada.getCurso());
        return guardada;
    }

    @Transactional
    public void eliminar(Long id) {
        AsignaturaCurso relacion = obtener(id);
        Usuario docente = relacion.getDocente();
        Curso curso = relacion.getCurso();

        asignaturaCursoRepository.delete(relacion);
        asignaturaCursoRepository.flush();
        limpiarUsuarioCursoSiCorresponde(docente, curso);
    }

    private void asegurarUsuarioCurso(Usuario docente, Curso curso) {
        if (docente == null || curso == null) {
            return;
        }
        if (!usuarioCursoRepository.existsByUsuarioIdAndCursoId(docente.getId(), curso.getId())) {
            usuarioCursoRepository.save(UsuarioCurso.builder()
                    .usuario(docente)
                    .curso(curso)
                    .build());
        }
    }

    private void limpiarUsuarioCursoSiCorresponde(Usuario docente, Curso curso) {
        if (docente == null || curso == null) {
            return;
        }
        if (asignaturaCursoRepository.countByDocenteIdAndCursoId(
                docente.getId(), curso.getId()) == 0) {
            usuarioCursoRepository.findByUsuarioIdAndCursoId(
                            docente.getId(), curso.getId())
                    .ifPresent(usuarioCursoRepository::delete);
        }
    }

    /**
     * Regla de negocio: un curso solo puede ofrecer asignaturas de su nivel.
     * El esquema no lo expresa porque `curso.nivel_id` y `asignatura.nivel_id`
     * son FKs independientes.
     */
    private void validarNivel(Curso curso, Asignatura asignatura) {
        if (!curso.getNivel().getId().equals(asignatura.getNivel().getId())) {
            throw new ReglaNegocioException(
                    "La asignatura no pertenece al nivel del curso");
        }
    }

    private Curso resolverCurso(Curso curso) {
        if (curso == null || curso.getId() == null) {
            throw new ReglaNegocioException("El campo curso es obligatorio");
        }
        return cursoRepository.findById(curso.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Curso", curso.getId()));
    }

    private Asignatura resolverAsignatura(Asignatura asignatura) {
        if (asignatura == null || asignatura.getId() == null) {
            throw new ReglaNegocioException("El campo asignatura es obligatorio");
        }
        return asignaturaRepository.findById(asignatura.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Asignatura", asignatura.getId()));
    }

    private Usuario resolverDocente(Usuario docente) {
        if (docente == null) {
            return null;
        }
        if (docente.getId() == null) {
            throw new ReglaNegocioException("El campo docente debe incluir un identificador");
        }
        return resolverDocentePorId(docente.getId());
    }

    private Usuario resolverDocentePorId(Long docenteId) {
        if (docenteId == null) {
            return null;
        }
        Usuario usuario = usuarioRepository.findById(docenteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", docenteId));
        if (usuario.getRol() == null
                || !ROL_DOCENTE.equalsIgnoreCase(usuario.getRol().getNombre())) {
            throw new ReglaNegocioException(
                    "El usuario asignado debe tener el rol DOCENTE");
        }
        return usuario;
    }
}
