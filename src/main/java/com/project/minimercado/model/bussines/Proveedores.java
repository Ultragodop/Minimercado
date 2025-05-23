package com.project.minimercado.model.bussines;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "proveedores", schema = "minimercado")
public class Proveedores {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor", nullable = false)
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "telefono", length = 50)
    private String telefono;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "direccion")
    private String direccion;

    @OneToMany(mappedBy = "idProveedor")
    private Set<PedidosProveedor> pedidosProveedors = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idProveedor")
    private Set<Producto> productos = new LinkedHashSet<>();

}