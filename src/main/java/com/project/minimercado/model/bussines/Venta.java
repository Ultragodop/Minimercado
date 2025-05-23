package com.project.minimercado.model.bussines;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "ventas", schema = "minimercado", indexes = {
        @Index(name = "idx_venta_user", columnList = "id_usuario")
})
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta", nullable = false)
    private Integer id;

    @Column(name = "fecha", nullable = false)
    private Instant fecha;

    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario idUsuario;

    @Lob
    @Column(name = "tipo_pago", nullable = false)
    private String tipoPago;

    @OneToMany(mappedBy = "idVenta")
    private Set<DetalleVenta> detalleVentas = new LinkedHashSet<>();

}