package com.project.minimercado.model.bussines;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;


import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "productos", schema = "minimercado", indexes = {
        @Index(name = "idx_prod_cat", columnList = "id_categoria"),
        @Index(name = "idx_prod_prov", columnList = "id_proveedor")
})
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto", nullable = false)
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Lob
    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "precio_compra", nullable = false, precision = 10, scale = 2)
    private Double precioCompra;

    @Column(name = "precio_venta", nullable = false, precision = 10, scale = 2)
    private Double precioVenta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria idCategoria;

    @ColumnDefault("0")
    @Column(name = "stock_actual", nullable = false)
    private Integer stockActual;

    @ColumnDefault("0")
    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_proveedor", nullable = false)
    private com.project.minimercado.model.bussines.Proveedores idProveedor;

    @ColumnDefault("1")
    @Column(name = "activo", nullable = false)
    private Boolean activo = false;

    @OneToMany(mappedBy = "idProducto")
    private Set<DetallePedidoProveedor> detallePedidoProveedors = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idProducto")
    private Set<DetalleVenta> detalleVentas = new LinkedHashSet<>();

}