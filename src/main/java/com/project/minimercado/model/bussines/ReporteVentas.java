package com.project.minimercado.model.bussines;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "reportes_ventas", schema = "minimercado")
public class ReporteVentas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reporte", nullable = false)
    private Integer id;

    @Column(name = "fecha", nullable = false)
    private Instant fecha;

    @Column(name = "total_ventas", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalVentas;

    @Column(name = "cantidad_transacciones", nullable = false)
    private Integer cantidadTransacciones;

    @Column(name = "metodo_pago", nullable = false)
    private String metodoPago;

    @Column(name = "ticket_promedio", nullable = false, precision = 12, scale = 2)
    private BigDecimal ticketPromedio;

    @Column(name = "periodo", nullable = false)
    private String periodo; // DIARIO, SEMANAL, MENSUAL

    @Column(name = "fecha_inicio", nullable = false)
    private Instant fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private Instant fechaFin;
} 