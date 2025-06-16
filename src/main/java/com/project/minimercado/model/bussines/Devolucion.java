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
@Table(name = "devoluciones", schema = "minimercado", indexes = {
        @Index(name = "idx_dev_venta", columnList = "id_venta"),
        @Index(name = "idx_dev_usuario", columnList = "id_usuario")
})
public class Devolucion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_devolucion", nullable = false)
    private Integer id;

    @Column(name = "fecha", nullable = false)
    private Instant fecha;

    @Column(name = "motivo", nullable = false)
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoDevolucion estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta", nullable = false, foreignKey = @ForeignKey(name = "fk_devolucion_venta"))
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false, foreignKey = @ForeignKey(name = "fk_devolucion_usuario"))
    private Usuario usuario;

    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @OneToMany(mappedBy = "devolucion", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DetalleDevolucion> detallesDevolucion = new LinkedHashSet<>();

    @Column(name = "fecha_aprobacion")
    private Instant fechaAprobacion;

    @Column(name = "usuario_aprobacion")
    private String usuarioAprobacion;

    @Column(name = "comentario_aprobacion")
    private String comentarioAprobacion;

    @PrePersist
    protected void onCreate() {
        fecha = Instant.now();
        estado = EstadoDevolucion.PENDIENTE;
    }
} 