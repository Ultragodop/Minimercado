package com.project.minimercado.controllers.bussines;

import com.project.minimercado.dto.bussines.Analisis.AnalisisProductoDTO;
import com.project.minimercado.model.bussines.AnalisisProducto;
import com.project.minimercado.model.bussines.ReporteVentas;
import com.project.minimercado.services.bussines.ReportesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
@Slf4j
public class ReportesController {
    private final ReportesService reportesService;

    public ReportesController(ReportesService reportesService) {
        this.reportesService = reportesService;
    }

    @GetMapping("/ventas/diario/{fecha}")
    public ResponseEntity<ReporteVentas> obtenerReporteDiarioVentas(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        try {
            return ResponseEntity.ok(reportesService.generarReporteDiarioVentas(fecha));
        } catch (Exception e) {
            log.error("Error al generar reporte diario de ventas", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/ventas/periodo")
    public ResponseEntity<List<ReporteVentas>> obtenerReportePeriodoVentas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        try {
            return ResponseEntity.ok(reportesService.generarReportePeriodoVentas(fechaInicio, fechaFin));
        } catch (Exception e) {
            log.error("Error al generar reporte de periodo de ventas", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/ventas/metodo-pago")
    public ResponseEntity<Map<String, List<ReporteVentas>>> obtenerReporteMetodoPago(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        try {
            return ResponseEntity.ok(reportesService.generarReporteMetodoPago(fechaInicio, fechaFin));
        } catch (Exception e) {
            log.error("Error al generar reporte por método de pago", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/productos/ranking")
    public ResponseEntity<List<AnalisisProductoDTO>> obtenerRankingProductos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        try {
            return ResponseEntity.ok(reportesService.generarRankingProductos(fechaInicio, fechaFin));
        } catch (Exception e) {
            log.error("Error al generar ranking de productos", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/productos/rentabilidad")
    public ResponseEntity<List<AnalisisProductoDTO>> obtenerAnalisisRentabilidad(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        try {
            return ResponseEntity.ok(reportesService.generarAnalisisRentabilidad(fechaInicio, fechaFin));
        } catch (Exception e) {
            log.error("Error al generar análisis de rentabilidad", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/productos/rotacion")
    public ResponseEntity<List<AnalisisProductoDTO>> obtenerAnalisisRotacion(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        try {
            return ResponseEntity.ok(reportesService.generarAnalisisRotacion(fechaInicio, fechaFin));
        } catch (Exception e) {
            log.error("Error al generar análisis de rotación", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/actualizar")
    public ResponseEntity<Void> actualizarReportes(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        try {
            reportesService.actualizarAnalisisProductos(fechaInicio, fechaFin);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al actualizar reportes", e);
            return ResponseEntity.internalServerError().build();
        }
    }
} 