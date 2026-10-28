package com.example.mpaz_pro_6springboot.docencia.docencia;

import com.example.mpaz_pro_6springboot.common.PostgresIntegrationTest;
import com.example.mpaz_pro_6springboot.docencia.docencia.model.DocenteCursoAsignatura;
import com.example.mpaz_pro_6springboot.docencia.docencia.repository.DocenteCursoAsignaturaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DocenciaSecurityTest extends PostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DocenteCursoAsignaturaRepository docenciaRepository;

    @Test
    void elDocenteVeSoloSusDocencias() throws Exception {
        mockMvc.perform(get("/api/docencias")
                        .with(bearer("docente")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(8)))
                .andExpect(jsonPath("$[0].docenteNombre").value("Diego Salinas"))
                .andExpect(jsonPath("$[0].cursoNombre").isNotEmpty())
                .andExpect(jsonPath("$[0].asignaturaNombre").isNotEmpty());

        mockMvc.perform(get("/api/docencias")
                        .with(bearer("utp")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(8)));
    }

    @Test
    void elEstudianteNoPuedeConsultarDocencias() throws Exception {
        mockMvc.perform(get("/api/docencias")
                        .with(bearer("camila")))
                .andExpect(status().isForbidden());
    }

    @Test
    void laUtpNoPuedeCrearDocenciasDuplicadas() throws Exception {
        DocenteCursoAsignatura existente = docenciaRepository
                .findAllByOrderByIdAsc()
                .getFirst();

        mockMvc.perform(post("/api/docencias")
                        .with(bearer("utp"))
                        .contentType("application/json")
                        .content("""
                                {
                                  "docenteId": %d,
                                  "cursoId": %d,
                                  "asignaturaId": %d
                                }
                                """.formatted(
                                existente.getDocente().getId(),
                                existente.getCurso().getId(),
                                existente.getAsignatura().getId()
                        )))
                .andExpect(status().isConflict());
    }

    @Test
    void noSePuedeEliminarUnaDocenciaConAsignaciones() throws Exception {
        DocenteCursoAsignatura docencia = docenciaRepository
                .findAllByOrderByIdAsc()
                .getFirst();

        mockMvc.perform(delete("/api/docencias/{id}", docencia.getId())
                        .with(bearer("utp")))
                .andExpect(status().isConflict());
    }

    @Test
    void elDocenteNoPuedeCrearDocencias() throws Exception {
        mockMvc.perform(post("/api/docencias")
                        .with(bearer("docente"))
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isForbidden());
    }
}
