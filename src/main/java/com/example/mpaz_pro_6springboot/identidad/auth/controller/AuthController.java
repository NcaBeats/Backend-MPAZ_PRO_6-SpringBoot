package com.example.mpaz_pro_6springboot.identidad.auth.controller;

import com.example.mpaz_pro_6springboot.identidad.auth.service.AuthService;
import com.example.mpaz_pro_6springboot.identidad.usuario.dto.request.UsuarioCreateRequest;
import com.example.mpaz_pro_6springboot.identidad.usuario.dto.response.UsuarioResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    /**
     * Login con HTTP Basic: las credenciales viajan en Authorization y el
     * filtro de Spring Security las valida antes de llegar al Controller.
     */
    @PostMapping("/login")
    public ResponseEntity<UsuarioResponse> login(Authentication authentication) {
        return ResponseEntity.ok(authService.login(authentication));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> me(Authentication authentication) {
        return ResponseEntity.ok(authService.me(authentication));
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioResponse> register(@Valid @RequestBody UsuarioCreateRequest request) {
        UsuarioResponse created = authService.register(request);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/usuarios/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }
}
