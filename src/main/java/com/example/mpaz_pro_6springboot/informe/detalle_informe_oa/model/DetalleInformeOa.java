package com.example.mpaz_pro_6springboot.informe.detalle_informe_oa.model;

import com.example.mpaz_pro_6springboot.common.enums.NivelOa;
import com.example.mpaz_pro_6springboot.informe.informe_pedagogico.model.InformePedagogico;
import com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.model.ObjetivoAprendizaje;
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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "detalle_informe_oa", uniqueConstraints = {
    @UniqueConstraint(name = "uk_detalle_informe_oa", columnNames = {"informe_id", "oa_id"})
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"informe", "oa"})
public class DetalleInformeOa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "informe_id", nullable = false)
    private InformePedagogico informe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oa_id", nullable = false)
    private ObjetivoAprendizaje oa;

    @Column(name = "total_preguntas", nullable = false)
    private Integer totalPreguntas;

    @Column(name = "correctas", nullable = false)
    private Integer correctas;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel", nullable = false, length = 20)
    private NivelOa nivel;
}
