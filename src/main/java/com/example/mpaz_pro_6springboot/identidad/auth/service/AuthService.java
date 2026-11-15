package com.example.mpaz_pro_6springboot.identidad.auth.service;

import com.example.mpaz_pro_6springboot.common.security.CustomUserDetails;
import com.example.mpaz_pro_6springboot.common.security.JwtService;
import com.example.mpaz_pro_6springboot.identidad.auth.dto.request.LoginRequest;
import com.example.mpaz_pro_6springboot.identidad.auth.dto.response.LoginResponse;
import com.example.mpaz_pro_6springboot.identidad.dto.request.UsuarioRequest;
import com.example.mpaz_pro_6springboot.identidad.dto.response.UsuarioResponse;
import com.example.mpaz_pro_6springboot.identidad.mapper.UsuarioMapper;
import com.example.mpaz_pro_6springboot.identidad.model.Usuario;
import com.example.mpaz_pro_6springboot.identidad.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Autenticacion stateless con JWT.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

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
        if (authentication == null
                || !(authentication.getPrincipal() instanceof CustomUserDetails principal)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
        return usuarioMapper.toResponse(principal.getUsuario());
    }

    /**
     * Registro administrativo. El controlador solo permite llegar aqui a un
     * usuario con rol UTP.
     */
    public UsuarioResponse register(UsuarioRequest request) {
        Usuario datos = usuarioMapper.toEntity(request);
        datos.setPasswordHash(passwordEncoder.encode(request.password()));
        Usuario creado = usuarioService.crear(datos);
        return usuarioMapper.toResponse(creado);
    }
}
