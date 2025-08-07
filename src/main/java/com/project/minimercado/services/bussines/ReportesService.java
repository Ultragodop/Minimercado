package com.project.minimercado.services.bussines;

import com.project.minimercado.model.bussines.*;
import com.project.minimercado.repository.bussines.AnalisisProductoRepository;
import com.project.minimercado.repository.bussines.ProductosRepository;
import com.project.minimercado.repository.bussines.ReporteVentasRepository;
import com.project.minimercado.repository.bussines.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportesService {
    private final VentaRepository ventaRepository;
    private final ReporteVentasRepository reporteVentasRepository;
    private final AnalisisProductoRepository analisisProductoRepository;
    private final ProductosRepository productosRepository;

    public ReportesService(
            VentaRepository ventaRepository,
            ReporteVentasRepository reporteVentasRepository,
            AnalisisProductoRepository analisisProductoRepository,
            ProductosRepository productosRepository) {
        this.ventaRepository = ventaRepository;
        this.reporteVentasRepository = reporteVentasRepository;
        this.analisisProductoRepository = analisisProductoRepository;
        this.productosRepository = productosRepository;
    }

    @Transactional
    public ReporteVentas generarReporteDiarioVentas(LocalDate fecha) {
        Instant inicioDia = fecha.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant finDia = fecha.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        return generarReporteVentas(inicioDia, finDia, "DIARIO");
    }

    @Transactional
    public ReporteVentas generarReporteSemanalVentas(LocalDate fechaInicio, LocalDate fechaFin) {
        Instant inicioSemana = fechaInicio.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant finSemana = fechaFin.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        return generarReporteVentas(inicioSemana, finSemana, "SEMANA");
    }

    @Transactional
    public List<ReporteVentas> generarReportePeriodoVentas(LocalDate fechaInicio, LocalDate fechaFin) {
        Instant inicio = fechaInicio.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant fin = fechaFin.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        return reporteVentasRepository.findByPeriodo(inicio, fin);
    }

    @Transactional
    public Map<String, List<ReporteVentas>> generarReporteMetodoPago(LocalDate fechaInicio, LocalDate fechaFin) {
        Instant inicio = fechaInicio.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant fin = fechaFin.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        List<ReporteVentas> reportes = reporteVentasRepository.findByPeriodo(inicio, fin);
        return reportes.stream()
                .collect(Collectors.groupingBy(ReporteVentas::getMetodoPago));
    }

    @Transactional
    public List<AnalisisProducto> generarRankingProductos(LocalDate fechaInicio, LocalDate fechaFin) {
        Instant inicio = fechaInicio.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant fin = fechaFin.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        return analisisProductoRepository.findTopVendidos(inicio, fin);
    }

    @Transactional
    public List<AnalisisProducto> generarAnalisisRentabilidad(LocalDate fechaInicio, LocalDate fechaFin) {
        Instant inicio = fechaInicio.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant fin = fechaFin.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        return analisisProductoRepository.findTopRentables(inicio, fin);
    }

    @Transactional
    public List<AnalisisProducto> generarAnalisisRotacion(LocalDate fechaInicio, LocalDate fechaFin) {
        Instant inicio = fechaInicio.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant fin = fechaFin.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        return analisisProductoRepository.findTopRotacion(inicio, fin);
    }
@Transactional
protected ReporteVentas generarReporteVentas(Instant fechaInicio, Instant fechaFin, String periodo) {
        List<Venta> ventas = ventaRepository.findAll().stream()
                .filter(v -> v.getFecha().isAfter(fechaInicio) && v.getFecha().isBefore(fechaFin))
                .toList();

        ReporteVentas reporte = new ReporteVentas();
        reporte.setFecha(Instant.now());
        reporte.setFechaInicio(fechaInicio);
        reporte.setFechaFin(fechaFin);
        reporte.setPeriodo(periodo);

        // Calcular métricas
        BigDecimal totalVentas = ventas.stream()
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int cantidadTransacciones = ventas.size();

        BigDecimal ticketPromedio = cantidadTransacciones > 0
                ? totalVentas.divide(BigDecimal.valueOf(cantidadTransacciones), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        reporte.setTotalVentas(totalVentas);
        reporte.setCantidadTransacciones(cantidadTransacciones);
        reporte.setTicketPromedio(ticketPromedio);

        // Agrupar por método de pago
        Map<String, List<Venta>> ventasPorMetodo = ventas.stream()
                .collect(Collectors.groupingBy(Venta::getTipoPago));

        // Crear reportes por método de pago
        for (Map.Entry<String, List<Venta>> entry : ventasPorMetodo.entrySet()) {
            ReporteVentas reporteMetodo = new ReporteVentas();
            reporteMetodo.setFecha(Instant.now());
            reporteMetodo.setFechaInicio(fechaInicio);
            reporteMetodo.setFechaFin(fechaFin);
            reporteMetodo.setPeriodo(periodo);
            reporteMetodo.setMetodoPago(entry.getKey());

            List<Venta> ventasMetodo = entry.getValue();
            BigDecimal totalMetodo = ventasMetodo.stream()
                    .map(Venta::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            reporteMetodo.setTotalVentas(totalMetodo);
            reporteMetodo.setCantidadTransacciones(ventasMetodo.size());
            reporteMetodo.setTicketPromedio(
                    !ventasMetodo.isEmpty()
                            ? totalMetodo.divide(BigDecimal.valueOf(ventasMetodo.size()), 2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO
            );

            reporteVentasRepository.save(reporteMetodo);
        }

        return reporte;
    }

    @Transactional
    public void actualizarAnalisisProductos(LocalDate fechaInicio, LocalDate fechaFin) {
        Instant inicio = fechaInicio.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant fin = fechaFin.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        List<Producto> productos = productosRepository.findAll();
        List<Venta> ventas = ventaRepository.findAll().stream()
                .filter(v -> v.getFecha().isAfter(inicio) && v.getFecha().isBefore(fin))
                .toList();


        for (Producto producto : productos) {
            AnalisisProducto analisis = new AnalisisProducto();
            analisis.setProducto(producto);
            analisis.setFechaInicio(inicio);
            analisis.setFechaFin(fin);
            analisis.setPeriodo("PERIODO");

            // Calcular métricas
            int unidadesVendidas = ventas.stream()
                    .flatMap(v -> v.getDetalleVentas().stream())
                    .filter(d -> d.getIdProducto().getId().equals(producto.getId()))
                    .mapToInt(DetalleVenta::getCantidad)
                    .sum();

            BigDecimal ingresos = ventas.stream()
                    .flatMap(v -> v.getDetalleVentas().stream())
                    .filter(d -> d.getIdProducto().getId().equals(producto.getId()))
                    .map(DetalleVenta::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal margenGanancia = ingresos.subtract(
                    BigDecimal.valueOf(producto.getPrecioCompra() * unidadesVendidas)
            );

            int rotacion = unidadesVendidas / (producto.getStockActual() > 0 ? producto.getStockActual() : 1);

            analisis.setUnidadesVendidas(unidadesVendidas);
            analisis.setIngresos(ingresos);
            analisis.setMargenGanancia(margenGanancia);
            analisis.setRotacion(rotacion);

            analisisProductoRepository.save(analisis);
        }
    }
} 