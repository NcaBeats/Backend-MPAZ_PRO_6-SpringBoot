package com.example.mpaz_pro_6springboot.identidad.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mpaz_pro_6springboot.common.exception.ConflictoException;
import com.example.mpaz_pro_6springboot.common.exception.RecursoNoEncontradoException;
import com.example.mpaz_pro_6springboot.common.exception.ReglaNegocioException;
import com.example.mpaz_pro_6springboot.common.util.Validaciones;
import com.example.mpaz_pro_6springboot.identidad.model.Rol;
import com.example.mpaz_pro_6springboot.identidad.model.Usuario;
import com.example.mpaz_pro_6springboot.identidad.repository.RolRepository;
import com.example.mpaz_pro_6springboot.identidad.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de {@link Usuario}.
 *
 * <p>No aplica hashing de contrasena: la autenticacion aun no esta implementada.
 * Cuando se agregue, el servicio recibira la contrasena en claro y guardara el
 * hash BCrypt. Por ahora {@code passwordHash} se persiste tal como llega.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> listarActivos() {
        return usuarioRepository.findAllByActivoTrue();
    }

    public List<Usuario> listarPorRol(Long rolId) {
        return usuarioRepository.findAllByRolId(rolId);
    }

    public Usuario obtener(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", id));
    }

    public Usuario obtenerPorNombreUsuario(String nombreUsuario) {
        String limpio = Validaciones.textoObligatorio(nombreUsuario, "nombreUsuario");
        return usuarioRepository.findByNombreUsuario(limpio)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Usuario no encontrado con nombre de usuario " + limpio));
    }

    @Transactional
    public Usuario crear(Usuario datos) {
        Validaciones.requerido(datos, "usuario");

        String nombreUsuario = Validaciones.textoObligatorio(datos.getNombreUsuario(), "nombreUsuario");
        String passwordHash = Validaciones.textoObligatorio(datos.getPasswordHash(), "passwordHash");
        if (usuarioRepository.existsByNombreUsuario(nombreUsuario)) {
            throw new ConflictoException("Ya existe un usuario con nombre de usuario " + nombreUsuario);
        }

        datos.setId(null);
        datos.setPnombre(Validaciones.textoOpcional(datos.getPnombre()));
        datos.setSnombre(Validaciones.textoOpcional(datos.getSnombre()));
        datos.setApaterno(Validaciones.textoOpcional(datos.getApaterno()));
        datos.setAmaterno(Validaciones.textoOpcional(datos.getAmaterno()));
        datos.setNombreUsuario(nombreUsuario);
        datos.setPasswordHash(passwordHash);
        datos.setRol(resolverRol(datos.getRol()));
        return usuarioRepository.save(datos);
    }

    /**
     * Actualiza los datos editables. El estado {@code activo} se cambia con
     * {@link #cambiarActivo(Long, boolean)} para evitar desactivaciones
     * accidentales al actualizar solo datos personales.
     */
    @Transactional
    public Usuario actualizar(Long id, Usuario datos) {
        Validaciones.requerido(datos, "usuario");
        Usuario usuario = obtener(id);

        if (datos.getPnombre() != null) {
            usuario.setPnombre(Validaciones.textoOpcional(datos.getPnombre()));
        }
        if (datos.getSnombre() != null) {
            usuario.setSnombre(Validaciones.textoOpcional(datos.getSnombre()));
        }
        if (datos.getApaterno() != null) {
            usuario.setApaterno(Validaciones.textoOpcional(datos.getApaterno()));
        }
        if (datos.getAmaterno() != null) {
            usuario.setAmaterno(Validaciones.textoOpcional(datos.getAmaterno()));
        }
        if (datos.getNombreUsuario() != null) {
            String nombreUsuario = Validaciones.textoObligatorio(datos.getNombreUsuario(), "nombreUsuario");
            usuarioRepository.findByNombreUsuario(nombreUsuario)
                    .filter(otro -> !otro.getId().equals(id))
                    .ifPresent(otro -> {
                        throw new ConflictoException(
                                "Ya existe un usuario con nombre de usuario " + nombreUsuario);
                    });
            usuario.setNombreUsuario(nombreUsuario);
        }
        if (datos.getPasswordHash() != null && !datos.getPasswordHash().isBlank()) {
            usuario.setPasswordHash(datos.getPasswordHash());
        }
        if (datos.getRol() != null) {
            usuario.setRol(resolverRol(datos.getRol()));
        }
        return usuario;
    }

    @Transactional
    public Usuario cambiarPassword(Long id, String passwordHash) {
        Usuario usuario = obtener(id);
        usuario.setPasswordHash(Validaciones.textoObligatorio(passwordHash, "passwordHash"));
        return usuario;
    }

    @Transactional
    public Usuario cambiarActivo(Long id, boolean activo) {
        Usuario usuario = obtener(id);
        usuario.setActivo(activo);
        return usuario;
    }

    @Transactional
    public void eliminar(Long id) {
        usuarioRepository.delete(obtener(id));
    }

    private Rol resolverRol(Rol rol) {
        if (rol == null || rol.getId() == null) {
            throw new ReglaNegocioException("El campo rol es obligatorio");
        }
        return rolRepository.findById(rol.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol", rol.getId()));
    }
}
