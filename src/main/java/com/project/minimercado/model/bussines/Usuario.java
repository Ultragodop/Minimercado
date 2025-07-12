package com.project.minimercado.model.bussines;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.minimercado.model.chat.SalaUsuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity

@Table(name = "usuarios", schema = "minimercado", uniqueConstraints = {
        @UniqueConstraint(name = "usuario", columnNames = {"nombre"})
})


public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario", nullable = false)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Lob
    @Column(name = "rol", nullable = false)
    private String rol;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<SalaUsuario> salasUsuario = new ArrayList<>();

    @OneToMany(mappedBy = "idUsuario")
    @JsonIgnore
    private Set<Venta> ventas = new LinkedHashSet<>();



}