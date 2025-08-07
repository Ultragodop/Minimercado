package com.project.minimercado.model.bussines;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "categorias", schema = "minimercado")
@Data
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria", nullable = false)
    private Integer id;

    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    @Column(name = "fecha_actualizacion")
    private Instant fechaActualizacion;

    @OneToMany(mappedBy = "idCategoria")
    private Set<Producto> productos = new LinkedHashSet<>();

    @PrePersist
    protected void onCreate() {
        fechaCreacion = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = Instant.now();
    }
}