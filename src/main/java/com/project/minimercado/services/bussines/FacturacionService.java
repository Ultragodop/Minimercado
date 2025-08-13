package com.project.minimercado.services.bussines;

import com.itextpdf.layout.properties.HorizontalAlignment;
import com.netflix.appinfo.ApplicationInfoManager;
import com.project.minimercado.dto.bussines.Facturacion.TicketDTO;
import com.project.minimercado.dto.bussines.Inventario.ProductoDTO;
import com.project.minimercado.model.bussines.DetalleVenta;
import com.project.minimercado.model.bussines.EstadoTicket;
import com.project.minimercado.model.bussines.Ticket;
import com.project.minimercado.model.bussines.Venta;
import com.project.minimercado.model.peticiones.Response;
import com.project.minimercado.repository.bussines.ProductosRepository;
import com.project.minimercado.repository.bussines.TicketRepository;
import com.project.minimercado.repository.bussines.VentaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.ZoneId;

@Service
@Slf4j
public class FacturacionService {
    private static final BigDecimal IVA = new BigDecimal("0.22"); // 22% IVA
    private static final DateTimeFormatter TICKET_NUMBER_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private final TicketRepository ticketRepository;
    private final VentaRepository ventaRepository;
    private final ProductosRepository productosRepository;

    public FacturacionService(TicketRepository ticketRepository, VentaRepository ventaRepository, ProductosRepository productosRepository) {
        this.ticketRepository = ticketRepository;
        this.ventaRepository = ventaRepository;
        this.productosRepository = productosRepository;
    }
    @Transactional
    public Response generarTicketTarjeta(String transactionExternalId) {
        try {
            log.info("Generando ticket");
            Venta venta = ventaRepository.findByTransactionExternalId(transactionExternalId)
                    .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

            Integer ventaId = venta.getId();
            if (!ticketRepository.findByVentaId(ventaId).isEmpty()) {
                throw new RuntimeException("Ya existe un ticket para esta venta");
            }
            log.info("Generando ticket");
            Ticket ticket = new Ticket();
            ticket.setVenta(venta);
            ticket.setNumeroTicket(generarNumeroTicket());
            ticket.setMetodoPago(venta.getTipoPago());
            ticket.setEstado(EstadoTicket.GENERADO);
            log.info ("Calculando totales");
            BigDecimal subtotal = venta.getTotal().divide(BigDecimal.ONE.add(IVA), 2, RoundingMode.HALF_UP);
            BigDecimal impuestos = venta.getTotal().subtract(subtotal);
            log.info("Calculando impuestos");
            ticket.setSubtotal(subtotal);
            ticket.setImpuestos(impuestos);
            ticket.setTotal(venta.getTotal());
            ticket.setFecha(Instant.now());
            log.info("Generando XML y PDF");
            String xmlContent = generarXML(ticket);
            byte[] pdfContent = generarPDF(ticket);

            ticket.setXmlContent(xmlContent);
            ticket.setPdfContent(pdfContent);
            guardarPDFEnArchivo(pdfContent, "tickets/" + ticket.getNumeroTicket() + ".pdf");

            log.info("XML y PDF generados correctamente");
            log.info("Guardando ticket en la base de datos");
            return new Response("200", "Ticket generado correctamente");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    @Transactional
    public Ticket generarTicket(Integer ventaId) {
        try {
            log.info("Generando ticket");
            Venta venta = ventaRepository.findById(ventaId)
                    .orElseThrow(() -> new RuntimeException("Venta no encontrada"));


            if (!ticketRepository.findByVentaId(ventaId).isEmpty()) {
                throw new RuntimeException("Ya existe un ticket para esta venta");
            }
        log.info("Generando ticket");
            Ticket ticket = new Ticket();
            ticket.setVenta(venta);
            ticket.setNumeroTicket(generarNumeroTicket());
            ticket.setMetodoPago(venta.getTipoPago());
            ticket.setEstado(EstadoTicket.GENERADO);
            log.info ("Calculando totales");
            BigDecimal subtotal = venta.getTotal().divide(BigDecimal.ONE.add(IVA), 2, RoundingMode.HALF_UP);
            BigDecimal impuestos = venta.getTotal().subtract(subtotal);
            log.info("Calculando impuestos");
            ticket.setSubtotal(subtotal);
            ticket.setImpuestos(impuestos);
            ticket.setTotal(venta.getTotal());
            ticket.setFecha(Instant.now());
            log.info("Generando XML y PDF");
            String xmlContent = generarXML(ticket);
            byte[] pdfContent = generarPDF(ticket);

            ticket.setXmlContent(xmlContent);
            ticket.setPdfContent(pdfContent);
            guardarPDFEnArchivo(pdfContent, "tickets/" + ticket.getNumeroTicket() + ".pdf");

            log.info("XML y PDF generados correctamente");
            log.info("Guardando ticket en la base de datos");
            return ticketRepository.save(ticket);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public Response anularTicket(String numeroTicket) {
        if(numeroTicket == null || numeroTicket.isEmpty()) {
            return new Response("400", "Número de ticket no puede ser nulo o vacío");
        }
        Ticket ticket = ticketRepository.findByNumeroTicket(numeroTicket)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado"));

        if (ticket.getEstado() == EstadoTicket.ANULADO) {
            return new Response("404", "El ticket ya está anulado");
        }

        ticket.setEstado(EstadoTicket.ANULADO);
        ticketRepository.save(ticket);
        return new Response("200", "Ticket anulado correctamente");
    }

    @Transactional(readOnly = true)
    public List<Ticket> obtenerTicketsPorFecha(Instant fechaInicio, Instant fechaFin) {
        return ticketRepository.findByFechaBetween(fechaInicio, fechaFin);
    }

    @Transactional(readOnly = true)
    public List<Ticket> obtenerTicketsPorEstado(EstadoTicket estado) {
        return ticketRepository.findByEstado(estado);
    }

    @Transactional(readOnly = true)
    public TicketDTO obtenerTicketPorNumero(String numeroTicket) {
        return ticketRepository.findByNumeroTicketDTO(numeroTicket);
    }

    private String generarNumeroTicket() {
        return LocalDateTime.now().format(TICKET_NUMBER_FORMAT) + "-" + UUID.randomUUID().toString().substring(0, 8);
    }


    private String generarXML(Ticket ticket) {
        try {

            if (ticket == null) {
                return "";
            }
            log.info("Generando XML para el ticket: {}", ticket.getNumeroTicket());
            StringBuilder xml = new StringBuilder();
            xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            xml.append("<ticket>\n");

            // Basic fields
            xml.append("    <id_ticket>").append(ticket.getId()).append("</id_ticket>\n");
            xml.append("    <numero_ticket>").append(ticket.getNumeroTicket()).append("</numero_ticket>\n");

            // Format Instant to ISO-8601
            String fechaFormatted = ticket.getFecha() != null
                    ? ticket.getFecha().toString()
                    : "";
            xml.append("    <fecha>").append(fechaFormatted).append("</fecha>\n");

            // Venta details (ID only to avoid recursion)
            xml.append("    <venta>\n");
            xml.append("        <id_venta>").append(ticket.getVenta().getId()).append("</id_venta>\n");
            xml.append("    </venta>\n");

            // Monetary values (using toPlainString to avoid scientific notation)
            xml.append("    <subtotal>").append(ticket.getSubtotal().toPlainString()).append("</subtotal>\n");
            xml.append("    <impuestos>").append(ticket.getImpuestos().toPlainString()).append("</impuestos>\n");
            xml.append("    <total>").append(ticket.getTotal().toPlainString()).append("</total>\n");

            // Payment and status
            xml.append("    <metodo_pago>").append(ticket.getMetodoPago()).append("</metodo_pago>\n");
            xml.append("    <estado>").append(ticket.getEstado().name()).append("</estado>\n");

            xml.append("</ticket>");

            return xml.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] generarPDF(Ticket ticket) {
        try {
        if (ticket == null) {
            return new byte[0];
        }
            log.info("Generando PDF para el ticket: {}", ticket.getNumeroTicket());
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);


            PdfFont headerFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            Paragraph companyHeader = new Paragraph("MINIMERCADO LA ESQUINA")
                    .setFont(headerFont)
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10);
            document.add(companyHeader);


            Paragraph title = new Paragraph("TICKET DE COMPRA #" + ticket.getNumeroTicket())
                    .setFont(headerFont)
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(title);


            DateTimeFormatter dateFormatter = DateTimeFormatter
                    .ofPattern("dd/MM/yyyy HH:mm:ss")
                    .withZone(ZoneId.systemDefault());
            Table ProductosTable = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                    .setWidth(UnitValue.createPercentValue(80))
                    .setMarginBottom(20)
                    .setHorizontalAlignment(HorizontalAlignment.CENTER);
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
            Table detailsTable = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                    .setWidth(UnitValue.createPercentValue(80))
                    .setMarginBottom(20)
                    .setHorizontalAlignment(HorizontalAlignment.CENTER);



            addDetailRow(detailsTable, "Fecha:", dateFormatter.format(ticket.getFecha()), normalFont);
            addDetailRow(detailsTable, "Venta ID:", ticket.getVenta().getId().toString(), normalFont);
            addDetailRow(detailsTable, "Estado:", ticket.getEstado().name(), normalFont);

            addDetailRow(detailsTable, "Método de pago:", ticket.getMetodoPago(), normalFont);
            document.add(detailsTable);
            Set<DetalleVenta> detalleVenta = ticket.getVenta().getDetalleVentas();

            for(DetalleVenta detalle : detalleVenta) {
                ProductoDTO productoDTO = productosRepository.findProductoDTOById(detalle.getIdProducto().getId());
                String nombreProducto = productoDTO.getNombre();
                addDetailRow(ProductosTable, "Producto:", nombreProducto, detalle.getCantidad().toString(),
                        currencyFormat.format(detalle.getIdProducto().getPrecioVenta()), normalFont);

            }
            document.add(ProductosTable);

            Table amountsTable = new Table(UnitValue.createPercentArray(new float[]{3, 1}))
                    .setWidth(UnitValue.createPercentValue(60))
                    .setMarginBottom(30)
                    .setHorizontalAlignment(HorizontalAlignment.RIGHT);

            addAmountRow(amountsTable, "Subtotal:", ticket.getSubtotal(), currencyFormat, normalFont);
            addAmountRow(amountsTable, "Impuestos:", ticket.getImpuestos(), currencyFormat, normalFont);
            addAmountRow(amountsTable, "TOTAL:", ticket.getTotal(), currencyFormat, headerFont);
            document.add(amountsTable);


            Paragraph footer = new Paragraph("¡Gracias por su compra!")
                    .setFont(normalFont)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setItalic()
                    .setMarginTop(30);
            document.add(footer);

            document.close();
            return baos.toByteArray();
        } catch (IOException e) {

            e.printStackTrace();
            return new byte[0];
        }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void guardarPDFEnArchivo(byte[] pdfContent, String relativePath) {
        try {
            String osName = System.getProperty("os.name").toLowerCase();
            log.info("Sistema operativo detectado: {}", osName);
          String carpetaBase;
            if(System.getProperty(osName).toLowerCase().contains("windows")) {
                carpetaBase = "C:/Users/mampfv/Downloads/Pdfs";
            }
            else{
            carpetaBase = "/home/santi/Pdfs";
            }
            log.info("Guardando PDF en la ruta: {} ",  carpetaBase );


            Path path = Paths.get(carpetaBase, relativePath);

            // Crea las carpetas necesarias si no existen
            Files.createDirectories(path.getParent());

            // Escribe el archivo PDF en disco
            Files.write(path, pdfContent);

            System.out.println("PDF guardado en: " + path.toAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al guardar el PDF", e);
        }
    }


    private void addDetailRow(Table table, String label, String value, PdfFont font) {
        table.addCell(new Paragraph(label).setFont(font).setBold());
        table.addCell(new Paragraph(value).setFont(font));

    }
    private void addDetailRow(Table table, String label, String nombre, String cantidad, String precio, PdfFont font) {
        table.addCell(new Paragraph(label).setFont(font).setBold());
        table.addCell(new Paragraph(nombre).setFont(font));
        table.addCell(new Paragraph(cantidad).setFont(font));
        table.addCell(new Paragraph(precio).setFont(font));
    }


    private void addAmountRow(Table table, String label, BigDecimal amount,
                              NumberFormat currencyFormat, PdfFont font) {
        table.addCell(new Paragraph(label).setFont(font));
        table.addCell(new Paragraph(currencyFormat.format(amount))
                .setFont(font)
                .setTextAlignment(TextAlignment.RIGHT));
    }

    public String obtenerXML(String numeroTicket) {
        if(numeroTicket == null || numeroTicket.isEmpty()) {
            throw new IllegalArgumentException("Número de ticket no puede ser nulo o vacío");
        }

        return ticketRepository.findByNumeroTicket(numeroTicket)
                .map(Ticket::getXmlContent)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado"));
    }

    public byte[] obtenerPDFPorNumeroTicket(String numeroTicket) {
        return ticketRepository.findByNumeroTicket(numeroTicket).map(Ticket::getPdfContent)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado"));


    }
}