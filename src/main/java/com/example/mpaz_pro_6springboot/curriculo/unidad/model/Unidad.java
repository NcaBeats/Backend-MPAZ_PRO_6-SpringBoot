package com.example.mpaz_pro_6springboot.curriculo.unidad;

import com.example.mpaz_pro_6springboot.curriculo.actividad.model.Actividad;
import com.example.mpaz_pro_6springboot.curriculo.asignatura.model.Asignatura;
import com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.model.ObjetivoAprendizaje;
import com.example.mpaz_pro_6springboot.docencia.asignacion.model.Asignacion;
import com.example.mpaz_pro_6springboot.identidad.usuario.model.Usuario;
import com.example.mpaz_pro_6springboot.informe.informe_pedagogico.model.InformePedagogico;
import com.example.mpaz_pro_6springboot.curriculo.unidad.enums.EstadoContenido;
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

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "unidad", uniqueConstraints = @UniqueConstraint(name = "uk_unidad_asignatura_titulo", columnNames = {"asignatura_id", "titulo"}))
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"asignatura", "autorizadaPor", "objetivosAprendizaje", "actividades", "asignaciones", "informes"})
public class Unidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asignatura_id", nullable = false)
    private Asignatura asignatura;

    @Column(name = "nivel_educativo", nullable = false, length = 50)
    @Builder.Default
    private String nivelEducativo = "Sexto Basico";

    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Column(name = "orden", nullable = false)
    @Builder.Default
    private Integer orden = 1;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private EstadoContenido estado = EstadoContenido.BORRADOR;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autorizada_por_id")
    private Usuario autorizadaPor;

    @Column(name = "fecha_autorizacion")
    private Instant fechaAutorizacion;

    @OneToMany(mappedBy = "unidad", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ObjetivoAprendizaje> objetivosAprendizaje = new ArrayList<>();

    @OneToMany(mappedBy = "unidad", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Actividad> actividades = new ArrayList<>();

    @OneToMany(mappedBy = "unidad", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Asignacion> asignaciones = new ArrayList<>();

    @OneToMany(mappedBy = "unidad", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InformePedagogico> informes = new ArrayList<>();
}