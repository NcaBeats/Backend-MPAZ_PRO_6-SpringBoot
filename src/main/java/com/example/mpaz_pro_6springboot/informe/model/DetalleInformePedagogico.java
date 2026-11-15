package com.example.mpaz_pro_6springboot.informe.model;

import com.example.mpaz_pro_6springboot.curriculo.model.ObjetivoAprendizaje;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Resultado por objetivo de aprendizaje dentro de un informe pedagogico.
 *
 * <p>El OA debe pertenecer a la unidad del informe; esa coherencia se valida
 * en la capa de servicio porque esta tabla no duplica {@code unidad_id}.</p>
 */
@Entity
@Table(
        name = "detalle_informe_pedagogico",
        uniqueConstraints = @UniqueConstraint(
                name = "detalle_informe_pedagogico__un",
                columnNames = {"informe_pedagogico_id", "objetivo_aprendizaje_id"}
        )
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"informePedagogico", "objetivoAprendizaje"})
public class DetalleInformePedagogico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detalle_ip_id")
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "informe_pedagogico_id", nullable = false)
    private InformePedagogico informePedagogico;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "objetivo_aprendizaje_id", nullable = false)
    private ObjetivoAprendizaje objetivoAprendizaje;

    @Column(name = "correctas", nullable = false)
    private Integer correctas;

    @Column(name = "incorrectas", nullable = false)
    private Integer incorrectas;
}
