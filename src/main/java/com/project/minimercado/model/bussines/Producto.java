package com.project.minimercado.model.bussines;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.util.Date;
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

    @Column(name = "precio_compra", nullable = false)
    private Double precioCompra;

    @Column(name = "precio_venta", nullable = false)
    private Double precioVenta;

    @Column(name="fechaVencimiento",nullable = false)
    private Date fechaVencimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false, foreignKey = @ForeignKey(name = "fk_producto_categoria"))
    private Categoria idCategoria;

    @ColumnDefault("0")
    @Column(name = "stock_actual", nullable = false)
    private Integer stockActual;


    @ColumnDefault("0")
    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proveedor", nullable = false, foreignKey = @ForeignKey(name = "fk_producto_proveedor"))
    private Proveedores idProveedor;

    @ColumnDefault("1")
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @OneToMany(mappedBy = "idProducto")
    private Set<DetallePedidoProveedor> detallePedidoProveedors = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idProducto")
    private Set<DetalleVenta> detalleVentas = new LinkedHashSet<>();
}