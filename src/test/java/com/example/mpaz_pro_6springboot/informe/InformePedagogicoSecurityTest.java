package com.example.mpaz_pro_6springboot.informe;

import com.example.mpaz_pro_6springboot.common.PostgresIntegrationTest;
import com.example.mpaz_pro_6springboot.curriculo.unidad.repository.UnidadRepository;
import com.example.mpaz_pro_6springboot.identidad.usuario.repository.UsuarioRepository;
import com.example.mpaz_pro_6springboot.informe.informe_pedagogico.model.InformePedagogico;
import com.example.mpaz_pro_6springboot.informe.informe_pedagogico.repository.InformePedagogicoRepository;
import com.example.mpaz_pro_6springboot.informe.informe_pedagogico.service.InformePedagogicoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Reglas de la seccion 8: un estudiante solo ve sus propios informes y no
 * puede generarlos ni validarlos. Los endpoints dejan de ser publicos.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class InformePedagogicoSecurityTest extends PostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private UnidadRepository unidadRepository;
    @Autowired
    private InformePedagogicoRepository informeRepository;
    @Autowired
    private InformePedagogicoService informePedagogicoService;

    @Test
    void elEstudianteSoloVeSusPropiosInformes() throws Exception {
        var camila = usuarioRepository.findByUsername("camila").orElseThrow();
        InformePedagogico informeCamila = informeRepository.findByEstudianteId(camila.getId()).getFirst();

        mockMvc.perform(get("/api/informes-pedagogicos")
                        .with(httpBasic("camila", "alu12345")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(informeCamila.getId()))
                .andExpect(jsonPath("$[0].estudianteId").value(camila.getId()))
                .andExpect(jsonPath("$[0].estudianteNombre").value("Camila Rivas"))
                .andExpect(jsonPath("$[0].unidadTitulo").isNotEmpty())
                .andExpect(jsonPath("$[0].asignaturaNombre").isNotEmpty())
                .andExpect(jsonPath("$[0].docenteNombre").value("Diego Salinas"));
    }

    @Test
    void elEstudianteNoPuedeConsultarAOtroEstudiante() throws Exception {
        var camila = usuarioRepository.findByUsername("camila").orElseThrow();
        var mateo = usuarioRepository.findByUsername("mateo").orElseThrow();
        var docente = usuarioRepository.findByUsername("docente").orElseThrow();
        var utp = usuarioRepository.findByUsername("utp").orElseThrow();
        var unidad = unidadRepository.findAllByOrderByAsignaturaIdAscOrdenAsc().getFirst();

        InformePedagogico informeMateo = informePedagogicoService.generar(
                mateo.getId(), unidad.getId(), docente.getId(), utp);

        mockMvc.perform(get("/api/informes-pedagogicos")
                        .param("estudianteId", mateo.getId().toString())
                        .with(httpBasic("camila", "alu12345")))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/informes-pedagogicos/{id}", informeMateo.getId())
                        .with(httpBasic("camila", "alu12345")))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/informes-pedagogicos/{id}/objetivos-aprendizaje", informeMateo.getId())
                        .with(httpBasic("camila", "alu12345")))
                .andExpect(status().isForbidden());

        // La UTP si puede consultar el informe de Mateo.
        mockMvc.perform(get("/api/informes-pedagogicos/{id}", informeMateo.getId())
                        .with(httpBasic("utp", "utp12345")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estudianteId").value(mateo.getId()));
    }

    @Test
    void elEstudianteNoPuedeGenerarNiValidarInformes() throws Exception {
        var camila = usuarioRepository.findByUsername("camila").orElseThrow();
        var docente = usuarioRepository.findByUsername("docente").orElseThrow();
        var unidad = unidadRepository.findAllByOrderByAsignaturaIdAscOrdenAsc().getFirst();
        InformePedagogico informeCamila = informeRepository.findByEstudianteId(camila.getId()).getFirst();

        mockMvc.perform(post("/api/informes-pedagogicos")
                        .with(httpBasic("camila", "alu12345"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"estudianteId":%d,"unidadId":%d,"docenteId":%d}
                                """.formatted(camila.getId(), unidad.getId(), docente.getId())))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch("/api/informes-pedagogicos/{id}/validacion", informeCamila.getId())
                        .with(httpBasic("camila", "alu12345")))
                .andExpect(status().isForbidden());
    }

    @Test
    void sinAutenticacionNoSePuedenLeerLosInformes() throws Exception {
        InformePedagogico informe = informeRepository.findAllByOrderByIdAsc().getFirst();

        mockMvc.perform(get("/api/informes-pedagogicos"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/informes-pedagogicos/{id}", informe.getId()))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/informes-pedagogicos/{id}/objetivos-aprendizaje", informe.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void elDetalleIncluyeLosDatosDelOaYLosPorcentajes() throws Exception {
        var camila = usuarioRepository.findByUsername("camila").orElseThrow();
        InformePedagogico informe = informeRepository.findByEstudianteId(camila.getId()).getFirst();

        mockMvc.perform(get("/api/informes-pedagogicos/{id}/objetivos-aprendizaje", informe.getId())
                        .with(httpBasic("camila", "alu12345")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].oaCodigo").isNotEmpty())
                .andExpect(jsonPath("$[0].oaDescripcion").isNotEmpty())
                .andExpect(jsonPath("$[0].oaEje").isNotEmpty())
                .andExpect(jsonPath("$[0].totalPreguntas").value(3))
                .andExpect(jsonPath("$[0].correctas").value(2))
                .andExpect(jsonPath("$[0].nivel").value("REFUERZO"));
    }
}
