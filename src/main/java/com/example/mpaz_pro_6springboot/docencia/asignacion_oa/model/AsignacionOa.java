package com.example.mpaz_pro_6springboot.docencia.asignacion_oa.model;

import com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.model.ObjetivoAprendizaje;
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
@Table(name = "asignacion_oa")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"asignacion", "objetivoAprendizaje"})
public class AsignacionOa {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private AsignacionOaId id;

    @MapsId("asignacionId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asignacion_id", insertable = false, updatable = false)
    private Asignacion asignacion;

    @MapsId("oaId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oa_id", insertable = false, updatable = false)
    private ObjetivoAprendizaje objetivoAprendizaje;

    @Column(name = "unidad_id", nullable = false)
    private Long unidadId;
}