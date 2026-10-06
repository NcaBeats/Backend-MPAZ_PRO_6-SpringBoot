package com.example.mpaz_pro_6springboot.evaluacion.respuesta_estudiante;

import com.example.mpaz_pro_6springboot.curriculo.alternativa.model.Alternativa;
import com.example.mpaz_pro_6springboot.evaluacion.intento.model.Intento;
import com.example.mpaz_pro_6springboot.curriculo.pregunta.model.Pregunta;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
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
@Table(name = "respuesta_estudiante", uniqueConstraints = {
    @UniqueConstraint(name = "uk_respuesta_intento_pregunta", columnNames = {"intento_id", "pregunta_id"})
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"intento", "pregunta", "alternativa"})
public class RespuestaEstudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intento_id", nullable = false)
    private Intento intento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pregunta_id", nullable = false)
    private Pregunta pregunta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "pregunta_id", referencedColumnName = "pregunta_id", insertable = false, updatable = false),
        @JoinColumn(name = "alternativa_id", referencedColumnName = "id", insertable = false, updatable = false)
    })
    private Alternativa alternativa;

    @Column(name = "alternativa_id", nullable = false)
    private Long alternativaId;

    @Column(name = "es_correcta", nullable = false)
    private Boolean esCorrecta;
}
