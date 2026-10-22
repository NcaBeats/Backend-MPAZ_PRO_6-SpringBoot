package com.example.mpaz_pro_6springboot.curriculo.actividad.model;

import com.example.mpaz_pro_6springboot.docencia.asignacion_actividad.model.AsignacionActividad;
import com.example.mpaz_pro_6springboot.common.enums.TipoActividad;
import com.example.mpaz_pro_6springboot.evaluacion.intento.model.Intento;
import com.example.mpaz_pro_6springboot.curriculo.pregunta.model.Pregunta;
import com.example.mpaz_pro_6springboot.curriculo.unidad.model.Unidad;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "actividad", uniqueConstraints = {
    // Soporta la FK compuesta de seccion 4: pregunta (actividad_id, unidad_id) -> actividad (id, unidad_id)
    @UniqueConstraint(name = "uk_actividad_id_unidad", columnNames = {"id", "unidad_id"})
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"unidad", "preguntas", "asignacionesActividad", "intentos"})
public class Actividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidad_id", nullable = false)
    private Unidad unidad;

    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoActividad tipo;

    @Column(name = "orden", nullable = false)
    @Builder.Default
    private Integer orden = 1;

    @OneToMany(mappedBy = "actividad", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Pregunta> preguntas = new ArrayList<>();

    @OneToMany(mappedBy = "actividad", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AsignacionActividad> asignacionesActividad = new ArrayList<>();

    @OneToMany(mappedBy = "actividad", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Intento> intentos = new ArrayList<>();
}
