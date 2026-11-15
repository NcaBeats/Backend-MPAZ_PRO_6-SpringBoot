package com.example.mpaz_pro_6springboot.identidad.model;

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
 * Usuario del sistema. El hash de la contrasena nunca se expone en toString.
 */
@Entity
@Table(
        name = "usuario",
        uniqueConstraints = @UniqueConstraint(
                name = "unique_u_nombre_u",
                columnNames = "nombre_usuario"
        )
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"passwordHash", "rol"})
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "pnombre", length = 25)
    private String pnombre;

    @Column(name = "snombre", length = 25)
    private String snombre;

    @Column(name = "apaterno", length = 25)
    private String apaterno;

    @Column(name = "amaterno", length = 25)
    private String amaterno;

    @Column(name = "nombre_usuario", nullable = false, length = 50)
    private String nombreUsuario;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    @Column(name = "activo", nullable = false)
    private boolean activo;
}
