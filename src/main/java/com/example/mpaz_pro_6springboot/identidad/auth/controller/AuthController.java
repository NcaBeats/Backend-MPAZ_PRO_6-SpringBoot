package com.example.mpaz_pro_6springboot.identidad.auth.controller;

import com.example.mpaz_pro_6springboot.identidad.auth.dto.request.LoginRequest;
import com.example.mpaz_pro_6springboot.identidad.auth.dto.response.LoginResponse;
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

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> me(Authentication authentication) {
        return ResponseEntity.ok(authService.me(authentication));
    }

    /**
     * El token es stateless: el cliente lo descarta. Se expone para que la app
     * pueda cerrar sesion de forma uniforme.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioResponse> register(
            @Valid @RequestBody UsuarioCreateRequest request
    ) {
        UsuarioResponse created = authService.register(request);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/usuarios/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }
}
