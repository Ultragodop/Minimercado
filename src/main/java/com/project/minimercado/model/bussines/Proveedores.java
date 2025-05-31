package com.project.minimercado.model.bussines;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
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

    @Column(name = "direccion", length = 200)
    private String direccion;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private Instant fechaActualizacion;

    @OneToMany(mappedBy = "idProveedor")
    private Set<Producto> productos = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idProveedor")
    private Set<PedidosProveedor> pedidosProveedors = new LinkedHashSet<>();

    @PrePersist
    protected void onCreate() {
        fechaCreacion = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = Instant.now();
    }
}