package com.example.mpaz_pro_6springboot.identidad.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.mpaz_pro_6springboot.identidad.dto.request.RolRequest;
import com.example.mpaz_pro_6springboot.identidad.dto.request.RolUpdateRequest;
import com.example.mpaz_pro_6springboot.identidad.dto.response.RolResponse;
import com.example.mpaz_pro_6springboot.identidad.mapper.RolMapper;
import com.example.mpaz_pro_6springboot.identidad.model.Rol;
import com.example.mpaz_pro_6springboot.identidad.service.RolService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * API REST del catalogo {@link Rol}.
 *
 * <p>La transaccion de clase mantiene abierta la sesion de Hibernate mientras
 * los mappers arman la respuesta. Con {@code open-in-view: false}, mapear
 * relaciones lazy fuera de una transaccion fallaria.</p>
 */
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Transactional
public class RolController {

    private final RolService rolService;
    private final RolMapper rolMapper;

    @GetMapping
    @Transactional(readOnly = true)
    public List<RolResponse> listar() {
        return rolMapper.toResponseList(rolService.listar());
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public RolResponse obtener(@PathVariable Long id) {
        return rolMapper.toResponse(rolService.obtener(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RolResponse crear(@Valid @RequestBody RolRequest request) {
        Rol creado = rolService.crear(request.nombre());
        return rolMapper.toResponse(creado);
    }

    @PutMapping("/{id}")
    public RolResponse actualizar(@PathVariable Long id,
            @Valid @RequestBody RolUpdateRequest request) {
        Rol actualizado = rolService.actualizar(id, request.nombre());
        return rolMapper.toResponse(actualizado);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        rolService.eliminar(id);
    }
}
