package com.project.minimercado.model.bussines;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "tickets", schema = "minimercado", indexes = {
    @Index(name = "idx_ticket_venta", columnList = "id_venta"),
    @Index(name = "idx_ticket_fecha", columnList = "fecha")
})
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ticket", nullable = false)
    private Integer id;

    @Column(name = "numero_ticket", nullable = false, unique = true)
    private String numeroTicket;

    @Column(name = "fecha", nullable = false)
    private Instant fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta", nullable = false, foreignKey = @ForeignKey(name = "fk_ticket_venta"))
    private Venta venta;

    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "impuestos", nullable = false, precision = 12, scale = 2)
    private BigDecimal impuestos;

    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(name = "metodo_pago", nullable = false)
    private String metodoPago;

    @Column(name = "estado", nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoTicket estado;

    @Column(name = "xml_content", columnDefinition = "TEXT")
    private String xmlContent;

    @Column(name = "pdf_content", columnDefinition = "LONGBLOB")
    private byte[] pdfContent;

    @PrePersist
    protected void onCreate() {
        fecha = Instant.now();
        estado = EstadoTicket.GENERADO;
    }
} 