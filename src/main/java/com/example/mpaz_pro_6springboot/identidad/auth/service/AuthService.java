package com.example.mpaz_pro_6springboot.identidad.auth.service;

import com.example.mpaz_pro_6springboot.common.security.CustomUserDetails;
import com.example.mpaz_pro_6springboot.identidad.usuario.dto.request.UsuarioCreateRequest;
import com.example.mpaz_pro_6springboot.identidad.usuario.dto.response.UsuarioResponse;
import com.example.mpaz_pro_6springboot.identidad.usuario.mapper.UsuarioMapper;
import com.example.mpaz_pro_6springboot.identidad.usuario.model.Usuario;
import com.example.mpaz_pro_6springboot.identidad.usuario.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

    public UsuarioResponse login(Authentication authentication) {
        return currentUser(authentication);
    }

    public UsuarioResponse me(Authentication authentication) {
        return currentUser(authentication);
    }

    /**
     * Registro administrativo: el Controller solo permite llegar aqui a un usuario UTP.
     */
    public UsuarioResponse register(UsuarioCreateRequest request) {
        Usuario creado = usuarioService.create(
                usuarioMapper.toEntity(request),
                request.cursoId(),
                request.password()
        );
        return usuarioMapper.toResponse(creado);
    }

    private UsuarioResponse currentUser(Authentication authentication) {
        if (authentication == null
                || !(authentication.getPrincipal() instanceof CustomUserDetails principal)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
        return usuarioMapper.toResponse(principal.getUsuario());
    }
}
