package com.example.mpaz_pro_6springboot.identidad.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.mpaz_pro_6springboot.identidad.dto.request.UsuarioRequest;
import com.example.mpaz_pro_6springboot.identidad.dto.request.UsuarioUpdateRequest;
import com.example.mpaz_pro_6springboot.identidad.dto.response.UsuarioResponse;
import com.example.mpaz_pro_6springboot.identidad.mapper.UsuarioMapper;
import com.example.mpaz_pro_6springboot.identidad.model.Usuario;
import com.example.mpaz_pro_6springboot.identidad.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * API REST de {@link Usuario}.
 *
 * <p>La contrasena viaja en claro en el request y se hashea con BCrypt en el
 * controlador mientras la autenticacion siga pendiente. {@code activo} no se
 * actualiza con el PUT: se cambia con {@code PUT /{id}/activo}.</p>
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Transactional
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar(@RequestParam(required = false) Long rolId) {
        List<Usuario> usuarios = rolId != null
                ? usuarioService.listarPorRol(rolId)
                : usuarioService.listar();
        return usuarioMapper.toResponseList(usuarios);
    }

    @GetMapping("/activos")
    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarActivos() {
        return usuarioMapper.toResponseList(usuarioService.listarActivos());
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public UsuarioResponse obtener(@PathVariable Long id) {
        return usuarioMapper.toResponse(usuarioService.obtener(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse crear(@Valid @RequestBody UsuarioRequest request) {
        Usuario datos = usuarioMapper.toEntity(request);
        datos.setPasswordHash(passwordEncoder.encode(request.password()));
        Usuario creado = usuarioService.crear(datos);
        return usuarioMapper.toResponse(creado);
    }

    @PutMapping("/{id}")
    public UsuarioResponse actualizar(@PathVariable Long id,
            @Valid @RequestBody UsuarioUpdateRequest request) {
        Usuario datos = usuarioMapper.toEntity(request);
        if (request.password() != null && !request.password().isBlank()) {
            datos.setPasswordHash(passwordEncoder.encode(request.password()));
        }
        Usuario actualizado = usuarioService.actualizar(id, datos);
        return usuarioMapper.toResponse(actualizado);
    }

    @PutMapping("/{id}/activo")
    public UsuarioResponse cambiarActivo(@PathVariable Long id,
            @RequestParam boolean activo) {
        return usuarioMapper.toResponse(usuarioService.cambiarActivo(id, activo));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
    }
}
