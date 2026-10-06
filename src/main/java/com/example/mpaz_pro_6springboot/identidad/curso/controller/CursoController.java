package com.example.mpaz_pro_6springboot.identidad.curso.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.mpaz_pro_6springboot.identidad.curso.mapper.CursoMapper;
import com.example.mpaz_pro_6springboot.identidad.curso.service.CursoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CursoController {

    private final CursoService cService;
    private final CursoMapper cMapper;
}
