package com.project.minimercado.controllers.bussines;

import com.project.minimercado.model.bussines.MovimientosContable;
import com.project.minimercado.services.bussines.TransactionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transacciones")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<List<MovimientosContable>> obtenerMovimientosPorFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(transactionService.obtenerMovimientosPorFecha(fecha));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<MovimientosContable>> obtenerMovimientosPorTipo(
            @PathVariable String tipo) {
        return ResponseEntity.ok(transactionService.obtenerMovimientosPorTipo(tipo));
    }

    @GetMapping("/balance/diario/{fecha}")
    public ResponseEntity<BigDecimal> obtenerBalanceDiario(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(transactionService.obtenerBalanceDiario(fecha));
    }

    @GetMapping("/balance/periodo")
    public ResponseEntity<BigDecimal> obtenerBalanceEntreFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return ResponseEntity.ok(transactionService.obtenerBalanceEntreFechas(fechaInicio, fechaFin));
    }

    @GetMapping("/ultimos/{cantidad}")
    public ResponseEntity<List<MovimientosContable>> obtenerUltimosMovimientos(
            @PathVariable int cantidad) {
        return ResponseEntity.ok(transactionService.obtenerUltimosMovimientos(cantidad));
    }

    @GetMapping("/referencia/{referencia}")
    public ResponseEntity<MovimientosContable> obtenerMovimientoPorReferencia(
            @PathVariable String referencia) {
        return ResponseEntity.ok(transactionService.obtenerMovimientoPorReferencia(referencia));
    }
} 