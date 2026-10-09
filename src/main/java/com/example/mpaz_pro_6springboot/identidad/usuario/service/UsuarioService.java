package com.example.mpaz_pro_6springboot.identidad.usuario.service;

import com.example.mpaz_pro_6springboot.common.enums.Rol;
import com.example.mpaz_pro_6springboot.curriculo.unidad.repository.UnidadRepository;
import com.example.mpaz_pro_6springboot.docencia.asignacion.repository.AsignacionRepository;
import com.example.mpaz_pro_6springboot.evaluacion.intento.repository.IntentoRepository;
import com.example.mpaz_pro_6springboot.identidad.curso.model.Curso;
import com.example.mpaz_pro_6springboot.identidad.curso.repository.CursoRepository;
import com.example.mpaz_pro_6springboot.identidad.usuario.model.Usuario;
import com.example.mpaz_pro_6springboot.identidad.usuario.repository.UsuarioRepository;
import com.example.mpaz_pro_6springboot.informe.informe_pedagogico.repository.InformePedagogicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final CursoRepository cursoRepository;
    private final IntentoRepository intentoRepository;
    private final InformePedagogicoRepository informePedagogicoRepository;
    private final AsignacionRepository asignacionRepository;
    private final UnidadRepository unidadRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Usuario> findAll() {
        return usuarioRepository.findAllByOrderByIdAsc();
    }

    public Usuario findById(Long id) {
        return getUsuario(id);
    }

    @Transactional
    public Usuario create(Usuario usuario, Long cursoId, String password) {
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un usuario con ese username"
            );
        }
        if (usuario.getRol() != Rol.ESTUDIANTE && cursoId != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Solo los estudiantes pueden tener un curso asignado"
            );
        }
        if (usuario.getRol() == Rol.ESTUDIANTE && cursoId != null) {
            usuario.setCurso(getCurso(cursoId));
        }
        usuario.setPasswordHash(passwordEncoder.encode(password));
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario update(Long id, Usuario cambios, Long cursoId, String password) {
        if (cambios.getNombre() == null
                && cambios.getUsername() == null
                && cambios.getRol() == null
                && cursoId == null
                && password == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe proporcionar al menos un campo para actualizar"
            );
        }

        Usuario usuario = getUsuario(id);
        Rol rolEfectivo = cambios.getRol() != null ? cambios.getRol() : usuario.getRol();

        if (cambios.getUsername() != null
                && usuarioRepository.existsByUsernameAndIdNot(cambios.getUsername(), id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un usuario con ese username"
            );
        }
        if (cursoId != null && rolEfectivo != Rol.ESTUDIANTE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Solo los estudiantes pueden tener un curso asignado"
            );
        }

        if (cursoId != null) {
            usuario.setCurso(getCurso(cursoId));
        }
        if (cambios.getRol() != null) {
            usuario.setRol(cambios.getRol());
            if (rolEfectivo != Rol.ESTUDIANTE) {
                usuario.setCurso(null);
            }
        }
        if (cambios.getNombre() != null) {
            usuario.setNombre(cambios.getNombre());
        }
        if (cambios.getUsername() != null) {
            usuario.setUsername(cambios.getUsername());
        }
        if (password != null) {
            usuario.setPasswordHash(passwordEncoder.encode(password));
        }
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void delete(Long id) {
        Usuario usuario = getUsuario(id);
        if (intentoRepository.existsByEstudianteId(id)
                || informePedagogicoRepository.existsByEstudianteId(id)
                || informePedagogicoRepository.existsByDocenteId(id)
                || unidadRepository.existsByAutorizadaPorId(id)
                || asignacionRepository.existsByDocenteId(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar un usuario con intentos, informes, unidades autorizadas o asignaciones asociadas"
            );
        }
        usuarioRepository.delete(usuario);
    }

    private Usuario getUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    private Curso getCurso(Long id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado"));
    }
}
