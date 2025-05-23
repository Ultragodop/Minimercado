package com.project.minimercado.model.bussines;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "pedidos_proveedor", schema = "minimercado", indexes = {
        @Index(name = "idx_pedprov_prov", columnList = "id_proveedor")
})
public class PedidosProveedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido", nullable = false)
    private Integer id;

    @Column(name = "fecha_pedido", nullable = false)
    private LocalDate fechaPedido;

    @Column(name = "fecha_entrega")
    private LocalDate fechaEntrega;

    @ColumnDefault("'pendiente'")
    @Lob
    @Column(name = "estado", nullable = false)
    private String estado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_proveedor", nullable = false)
    private com.project.minimercado.model.bussines.Proveedores idProveedor;

    @OneToMany(mappedBy = "idPedido")
    private Set<DetallePedidoProveedor> detallePedidoProveedors = new LinkedHashSet<>();

}