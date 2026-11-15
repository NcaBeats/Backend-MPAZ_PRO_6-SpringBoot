package com.example.mpaz_pro_6springboot.docencia.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.mpaz_pro_6springboot.common.exception.ConflictoException;
import com.example.mpaz_pro_6springboot.common.exception.RecursoNoEncontradoException;
import com.example.mpaz_pro_6springboot.common.exception.ReglaNegocioException;
import com.example.mpaz_pro_6springboot.common.security.SecurityUtils;
import com.example.mpaz_pro_6springboot.common.util.Validaciones;
import com.example.mpaz_pro_6springboot.docencia.model.UsuarioCurso;
import com.example.mpaz_pro_6springboot.docencia.repository.AsignaturaCursoRepository;
import com.example.mpaz_pro_6springboot.docencia.repository.UsuarioCursoRepository;
import com.example.mpaz_pro_6springboot.identidad.model.Curso;
import com.example.mpaz_pro_6springboot.identidad.model.Usuario;
import com.example.mpaz_pro_6springboot.identidad.repository.CursoRepository;
import com.example.mpaz_pro_6springboot.identidad.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de la relacion {@link UsuarioCurso}.
 *
 * <p>La tabla no distingue rol ni tipo de participacion; el servicio solo
 * valida existencia de las referencias y unicidad de la pareja.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioCursoService {

    private final UsuarioCursoRepository usuarioCursoRepository;
    private final AsignaturaCursoRepository asignaturaCursoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CursoRepository cursoRepository;

    public List<UsuarioCurso> listar() {
        List<UsuarioCurso> participaciones = usuarioCursoRepository.findAll();
        if (SecurityUtils.isEstudiante()) {
            Long usuarioId = SecurityUtils.requireUsuario().getId();
            return participaciones.stream()
                    .filter(participacion -> usuarioId.equals(
                            participacion.getUsuario().getId()))
                    .toList();
        }
        if (SecurityUtils.isDocente()) {
            Long docenteId = SecurityUtils.requireUsuario().getId();
            Set<Long> cursos = asignaturaCursoRepository.findAllByDocenteId(docenteId)
                    .stream()
                    .map(relacion -> relacion.getCurso().getId())
                    .collect(Collectors.toSet());
            return participaciones.stream()
                    .filter(participacion -> cursos.contains(
                            participacion.getCurso().getId()))
                    .toList();
        }
        return participaciones;
    }

    public List<UsuarioCurso> listarPorUsuario(Long usuarioId) {
        SecurityUtils.requireEstudiantePropietario(usuarioId, "cursos");
        return usuarioCursoRepository.findAllByUsuarioId(usuarioId);
    }

    public List<UsuarioCurso> listarPorCurso(Long cursoId) {
        if (SecurityUtils.isEstudiante()
                && !usuarioCursoRepository.existsByUsuarioIdAndCursoId(
                        SecurityUtils.requireUsuario().getId(), cursoId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Un estudiante solo puede consultar los participantes de su curso");
        }
        return usuarioCursoRepository.findAllByCursoId(cursoId);
    }

    public UsuarioCurso obtener(Long id) {
        return usuarioCursoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("UsuarioCurso", id));
    }

    public boolean existe(Long usuarioId, Long cursoId) {
        return usuarioCursoRepository.existsByUsuarioIdAndCursoId(usuarioId, cursoId);
    }

    @Transactional
    public UsuarioCurso crear(UsuarioCurso datos) {
        Validaciones.requerido(datos, "usuarioCurso");

        Usuario usuario = resolverUsuario(datos.getUsuario());
        Curso curso = resolverCurso(datos.getCurso());

        if (usuarioCursoRepository.existsByUsuarioIdAndCursoId(usuario.getId(), curso.getId())) {
            throw new ConflictoException("El usuario ya esta registrado en el curso indicado");
        }

        datos.setId(null);
        datos.setUsuario(usuario);
        datos.setCurso(curso);
        return usuarioCursoRepository.save(datos);
    }

    @Transactional
    public void eliminar(Long id) {
        usuarioCursoRepository.delete(obtener(id));
    }

    private Usuario resolverUsuario(Usuario usuario) {
        if (usuario == null || usuario.getId() == null) {
            throw new ReglaNegocioException("El campo usuario es obligatorio");
        }
        return usuarioRepository.findById(usuario.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", usuario.getId()));
    }

    private Curso resolverCurso(Curso curso) {
        if (curso == null || curso.getId() == null) {
            throw new ReglaNegocioException("El campo curso es obligatorio");
        }
        return cursoRepository.findById(curso.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Curso", curso.getId()));
    }
}
