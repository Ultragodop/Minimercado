package com.project.minimercado.model.bussines;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @Column(name = "id_venta", nullable = false)
    private Integer id;

    @Column(name = "fecha", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Montevideo")
    private Instant fecha;

    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario idUsuario;

    @Lob
    @Column(name = "tipo_pago", nullable = false)
    private String tipoPago;

    @Column(name = "estado", nullable = false)
    private String estado;

    @Column(name = "transaction_external_id")
    private String transactionExternalId;

    @OneToMany(mappedBy = "idVenta", fetch = FetchType.LAZY   , cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DetalleVenta> detalleVentas = new LinkedHashSet<>();


    public void setIdUsuario(Usuario usuario) {
        this.idUsuario = usuario;
    }
}