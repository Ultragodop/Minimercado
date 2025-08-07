package com.project.minimercado.dto.bussines.Facturacion;

import com.project.minimercado.model.bussines.EstadoTicket;
import com.project.minimercado.model.bussines.Ticket;

import java.math.BigDecimal;
import java.time.Instant;

public class TicketDTO {
    private Integer id;
    private String numeroTicket;
    private Instant fecha;
    private BigDecimal subtotal;
    private BigDecimal impuestos;
    private BigDecimal total;
    private String metodoPago;
    private EstadoTicket estado;
    private String xmlContent;
    private byte[] pdfContent;

    // Si querés incluir algo de la venta (por ejemplo, el ID):
    private Integer ventaId;

    // Constructor a partir de la entidad
    public TicketDTO(Ticket ticket) {
        this.id = ticket.getId();
        this.numeroTicket = ticket.getNumeroTicket();
        this.fecha = ticket.getFecha();
        this.subtotal = ticket.getSubtotal();
        this.impuestos = ticket.getImpuestos();
        this.total = ticket.getTotal();
        this.metodoPago = ticket.getMetodoPago();
        this.estado = ticket.getEstado();
        this.xmlContent = ticket.getXmlContent();
        this.pdfContent = ticket.getPdfContent();

        // Para evitar problemas de carga lazy
        this.ventaId = ticket.getVenta() != null ? ticket.getVenta().getId() : null;
    }

    // Getters y setters

    public Integer getId() {
        return id;
    }

    public String getNumeroTicket() {
        return numeroTicket;
    }

    public Instant getFecha() {
        return fecha;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getImpuestos() {
        return impuestos;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public EstadoTicket getEstado() {
        return estado;
    }

    public String getXmlContent() {
        return xmlContent;
    }

    public byte[] getPdfContent() {
        return pdfContent;
    }

    public Integer getVentaId() {
        return ventaId;
    }


}
