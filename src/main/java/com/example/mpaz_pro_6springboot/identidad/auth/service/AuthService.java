package com.example.mpaz_pro_6springboot.identidad.auth.service;

import com.example.mpaz_pro_6springboot.common.security.CustomUserDetails;
import com.example.mpaz_pro_6springboot.common.security.JwtService;
import com.example.mpaz_pro_6springboot.identidad.auth.dto.request.LoginRequest;
import com.example.mpaz_pro_6springboot.identidad.auth.dto.response.LoginResponse;
import com.example.mpaz_pro_6springboot.identidad.usuario.dto.request.UsuarioCreateRequest;
import com.example.mpaz_pro_6springboot.identidad.usuario.dto.response.UsuarioResponse;
import com.example.mpaz_pro_6springboot.identidad.usuario.mapper.UsuarioMapper;
import com.example.mpaz_pro_6springboot.identidad.usuario.model.Usuario;
import com.example.mpaz_pro_6springboot.identidad.usuario.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

    /**
     * Autentica las credenciales y emite un JWT stateless.
     */
    public LoginResponse login(LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
                    )
            );
        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Credenciales invalidas"
            );
        }

        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(principal);

        return new LoginResponse(
                token,
                "Bearer",
                jwtService.getExpirationSeconds(),
                usuarioMapper.toResponse(principal.getUsuario())
        );
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
