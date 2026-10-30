package com.example.mpaz_pro_6springboot.docencia.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mpaz_pro_6springboot.common.exception.ConflictoException;
import com.example.mpaz_pro_6springboot.common.exception.RecursoNoEncontradoException;
import com.example.mpaz_pro_6springboot.common.exception.ReglaNegocioException;
import com.example.mpaz_pro_6springboot.common.util.Validaciones;
import com.example.mpaz_pro_6springboot.docencia.model.UsuarioCurso;
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
    private final UsuarioRepository usuarioRepository;
    private final CursoRepository cursoRepository;

    public List<UsuarioCurso> listar() {
        return usuarioCursoRepository.findAll();
    }

    public List<UsuarioCurso> listarPorUsuario(Long usuarioId) {
        return usuarioCursoRepository.findAllByUsuarioId(usuarioId);
    }

    public List<UsuarioCurso> listarPorCurso(Long cursoId) {
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
