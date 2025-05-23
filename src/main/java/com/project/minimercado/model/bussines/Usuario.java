package com.project.minimercado.model.bussines;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity

@Table(name = "usuarios", schema = "minimercado", uniqueConstraints = {
        @UniqueConstraint(name = "usuario", columnNames = {"usuario"})
})
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario", nullable = false)
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Lob
    @Column(name = "rol", nullable = false)
    private String rol;

    @Column(name = "usuario", nullable = false, length = 100)
    private String usuario;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @OneToMany(mappedBy = "idUsuario")
    private Set<com.project.minimercado.model.bussines.Venta> ventas = new LinkedHashSet<>();

}