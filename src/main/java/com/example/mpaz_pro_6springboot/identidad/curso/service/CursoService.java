package com.example.mpaz_pro_6springboot.identidad.curso.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.mpaz_pro_6springboot.identidad.curso.model.Curso;
import com.example.mpaz_pro_6springboot.identidad.curso.repository.CursoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CursoService {

    private final CursoRepository cRepo;


    // --------------------------------------------
    //              MÉTODOS GET
    // --------------------------------------------
    public List<Curso> findAllCurso(){
        List<Curso> cursos = cRepo.findAll();
        return cursos;
    }

    public Curso findCursoById(Long id){
        Curso curso = cRepo.findById(id).orElse(null);
        return curso;
    }

    public List<Curso> findAllByCursoAnio(Integer anio){
        List<Curso> cursos = cRepo.findAllByAnio(anio);

        return cursos;
    }


    // --------------------------------------------
    //              MÉTODOS POST
    // --------------------------------------------

    public Curso saveCurso(Curso c){
        if(cRepo.existsByNombreAndAnio(c.getNombre(), c.getAnio())){
            return null;
        }

        return cRepo.save(c);
    }

    // --------------------------------------------
    //              MÉTODOS PUT
    // --------------------------------------------

    public Curso updateAnio(Long id, Integer anio){
        Curso c = cRepo.findById(id).orElse(null);

        if(c == null){
            return null;
        }

        c.setAnio(anio);
        return cRepo.save(c);
    }

    public Curso updateNombre(Long id, String nombre){
        Curso c = cRepo.findById(id).orElse(null);

        if(c == null){
            return null;
        }

        c.setNombre(nombre);
        return cRepo.save(c);
    }

    public Curso uodateNombreAnio(Long id, String nombre, Integer anio){
        Curso c = cRepo.findById(id).orElse(null);

        if(c == null){
            return null;
        }

        c.setNombre(nombre);
        c.setAnio(anio);
        return cRepo.save(c);
    }


    // --------------------------------------------
    //              MÉTODOS DELETE
    // --------------------------------------------

    public boolean deleteCurso(Long id){

        Curso c = cRepo.findById(id).orElse(null);

        if(c == null){
            return false;
        }

        cRepo.delete(c);

        return true;
    }
}
