package com.example.mpaz_pro_6springboot.identidad.usuario.controller;

import com.example.mpaz_pro_6springboot.docencia.asignacion.service.AsignacionService;
import com.example.mpaz_pro_6springboot.identidad.curso.dto.response.CursoResponse;
import com.example.mpaz_pro_6springboot.identidad.curso.mapper.CursoMapper;
import com.example.mpaz_pro_6springboot.identidad.usuario.dto.request.UsuarioCreateRequest;
import com.example.mpaz_pro_6springboot.identidad.usuario.dto.request.UsuarioUpdateRequest;
import com.example.mpaz_pro_6springboot.identidad.usuario.dto.response.UsuarioResponse;
import com.example.mpaz_pro_6springboot.identidad.usuario.mapper.UsuarioMapper;
import com.example.mpaz_pro_6springboot.identidad.usuario.model.Usuario;
import com.example.mpaz_pro_6springboot.identidad.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Validated
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;
    private final AsignacionService asignacionService;
    private final CursoMapper cursoMapper;

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> findAll() {
        return ResponseEntity.ok(usuarioService.findAll().stream()
                .map(usuarioMapper::toResponse)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> findById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(usuarioMapper.toResponse(usuarioService.findById(id)));
    }

    @GetMapping("/{id}/cursos")
    public ResponseEntity<List<CursoResponse>> findCursos(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(asignacionService.findCursosByDocente(id).stream()
                .map(cursoMapper::toResponse)
                .toList());
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> create(@Valid @RequestBody UsuarioCreateRequest request) {
        Usuario usuario = usuarioMapper.toEntity(request);
        UsuarioResponse created = usuarioMapper.toResponse(
                usuarioService.create(usuario, request.cursoId(), request.password())
        );
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> update(
            @PathVariable @Positive Long id,
            @Valid @RequestBody UsuarioUpdateRequest request
    ) {
        Usuario cambios = usuarioMapper.toEntity(request);
        return ResponseEntity.ok(usuarioMapper.toResponse(
                usuarioService.update(id, cambios, request.cursoId(), request.password())
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
