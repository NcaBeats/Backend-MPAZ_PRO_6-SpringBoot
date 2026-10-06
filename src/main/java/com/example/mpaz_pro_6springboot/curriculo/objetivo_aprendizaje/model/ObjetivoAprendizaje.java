package com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.model;

import com.example.mpaz_pro_6springboot.curriculo.contenido.model.Contenido;
import com.example.mpaz_pro_6springboot.curriculo.pregunta.model.Pregunta;
import com.example.mpaz_pro_6springboot.docencia.asignacion_oa.model.AsignacionOa;
import com.example.mpaz_pro_6springboot.informe.detalle_informe_oa.model.DetalleInformeOa;
import com.example.mpaz_pro_6springboot.curriculo.unidad.model.Unidad;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "objetivo_aprendizaje", uniqueConstraints = {
    @UniqueConstraint(name = "uk_oa_unidad_codigo", columnNames = {"unidad_id", "codigo"}),
    // Soporta las FK compuestas de seccion 4: (oa_id, unidad_id) desde pregunta, asignacion_oa y asignacion_actividad
    @UniqueConstraint(name = "uk_oa_id_unidad", columnNames = {"id", "unidad_id"})
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"unidad", "contenidos", "preguntas", "asignacionesOa", "detallesInforme"})
public class ObjetivoAprendizaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidad_id", nullable = false)
    private Unidad unidad;

    @Column(name = "codigo", length = 50)
    private String codigo;

    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "eje", nullable = false, length = 100)
    private String eje;

    @Column(name = "texto_referencia", length = 500)
    private String textoReferencia;

    @OneToMany(mappedBy = "oa", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Contenido> contenidos = new ArrayList<>();

    @OneToMany(mappedBy = "oa", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Pregunta> preguntas = new ArrayList<>();

    @OneToMany(mappedBy = "objetivoAprendizaje", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AsignacionOa> asignacionesOa = new ArrayList<>();

    @OneToMany(mappedBy = "oa", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DetalleInformeOa> detallesInforme = new ArrayList<>();
}