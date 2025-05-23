package com.project.minimercado.model.bussines;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "gastos", schema = "minimercado")
public class Gasto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_gasto", nullable = false)
    private Integer id;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "monto", nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Lob
    @Column(name = "categoria_gasto", nullable = false)
    private String categoriaGasto;

}