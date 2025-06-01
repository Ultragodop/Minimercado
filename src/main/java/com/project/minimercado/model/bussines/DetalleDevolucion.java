package com.project.minimercado.model.bussines;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "detalles_devolucion", schema = "minimercado", indexes = {
    @Index(name = "idx_det_dev_devolucion", columnList = "id_devolucion"),
    @Index(name = "idx_det_dev_producto", columnList = "id_producto")
})
public class DetalleDevolucion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_devolucion", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_devolucion", nullable = false, foreignKey = @ForeignKey(name = "fk_detalle_devolucion"))
    private Devolucion devolucion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false, foreignKey = @ForeignKey(name = "fk_detalle_devolucion_producto"))
    private Producto producto;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "motivo", nullable = false)
    private String motivo;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        if (cantidad != null && precioUnitario != null) {
            subtotal = precioUnitario.multiply(new BigDecimal(cantidad));
        }
    }
} 