package com.example.mpaz_pro_6springboot.curriculo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Actividad pedagogica. Es una plantilla reutilizable: hereda su unidad desde
 * el contenido asociado y su configuracion por curso (ventana, habilitacion y
 * limite de intentos) vive en {@code actividad_curso}.
 */
@Entity
@Table(name = "actividad")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"tipoActividad", "contenido", "estado"})
public class Actividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "actividad_id")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "instrucciones", nullable = false, columnDefinition = "text")
    private String instrucciones;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_actividad_id", nullable = false)
    private TipoActividad tipoActividad;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contenido_id", nullable = false)
    private Contenido contenido;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estado_id", nullable = false)
    private EstadoAutorizacion estado;
}
