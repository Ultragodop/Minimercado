package com.project.minimercado.model.bussines;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "analisis_productos", schema = "minimercado")
public class AnalisisProducto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_analisis", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @Column(name = "unidades_vendidas", nullable = false)
    private Integer unidadesVendidas;

    @Column(name = "ingresos", nullable = false, precision = 12, scale = 2)
    private BigDecimal ingresos;

    @Column(name = "margen_ganancia", nullable = false, precision = 12, scale = 2)
    private BigDecimal margenGanancia;

    @Column(name = "rotacion", nullable = false)
    private Integer rotacion;

    @Column(name = "fecha_inicio", nullable = false)
    private Instant fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private Instant fechaFin;

    @Column(name = "periodo", nullable = false)
    private String periodo; // DIARIO, SEMANAL, MENSUAL
} 