package com.example.mpaz_pro_6springboot.docencia.asignacion_actividad;

import com.example.mpaz_pro_6springboot.curriculo.actividad.model.Actividad;
import com.example.mpaz_pro_6springboot.curriculo.unidad.model.Unidad;
import com.example.mpaz_pro_6springboot.docencia.asignacion.model.Asignacion;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "asignacion_actividad")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"asignacion", "actividad"})
public class AsignacionActividad {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private AsignacionActividadId id;

    @MapsId("asignacionId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asignacion_id", insertable = false, updatable = false)
    private Asignacion asignacion;

    @MapsId("actividadId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actividad_id", insertable = false, updatable = false)
    private Actividad actividad;

    @Column(name = "unidad_id", nullable = false, insertable = false, updatable = false)
    private Long unidadId;
}