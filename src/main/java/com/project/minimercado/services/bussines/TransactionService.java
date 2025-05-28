package com.project.minimercado.services.bussines;

import com.project.minimercado.model.bussines.MovimientosContable;
import com.project.minimercado.repository.bussines.TransaccionesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class TransactionService {
    private final TransaccionesRepository transaccionesRepository;

    public TransactionService(TransaccionesRepository transaccionesRepository) {
        this.transaccionesRepository = transaccionesRepository;
    }

    @Transactional(readOnly = true)
    public List<MovimientosContable> obtenerMovimientosPorFecha(LocalDate fecha) {
        Instant inicioDia = fecha.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant finDia = fecha.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        return transaccionesRepository.findByFechaBetweenOrderByFechaDesc(inicioDia, finDia);
    }

    @Transactional(readOnly = true)
    public List<MovimientosContable> obtenerMovimientosPorTipo(String tipo) {
        return transaccionesRepository.findByTipoOrderByFechaDesc(tipo);
    }

    @Transactional(readOnly = true)
    public BigDecimal obtenerBalanceDiario(LocalDate fecha) {
        List<MovimientosContable> movimientos = obtenerMovimientosPorFecha(fecha);
        
        BigDecimal ingresos = movimientos.stream()
                .filter(m -> "INGRESO".equals(m.getTipo()))
                .map(MovimientosContable::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal egresos = movimientos.stream()
                .filter(m -> "EGRESO".equals(m.getTipo()))
                .map(MovimientosContable::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ingresos.subtract(egresos);
    }

    @Transactional(readOnly = true)
    public BigDecimal obtenerBalanceEntreFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        Instant inicio = fechaInicio.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant fin = fechaFin.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        List<MovimientosContable> movimientos = transaccionesRepository.findByFechaBetweenOrderByFechaDesc(inicio, fin);

        BigDecimal ingresos = movimientos.stream()
                .filter(m -> "INGRESO".equals(m.getTipo()))
                .map(MovimientosContable::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal egresos = movimientos.stream()
                .filter(m -> "EGRESO".equals(m.getTipo()))
                .map(MovimientosContable::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ingresos.subtract(egresos);
    }

    @Transactional(readOnly = true)
    public List<MovimientosContable> obtenerUltimosMovimientos(int cantidad) {
        return transaccionesRepository.findTopNByOrderByFechaDesc(cantidad);
    }

    @Transactional(readOnly = true)
    public MovimientosContable obtenerMovimientoPorReferencia(String referencia) {
        return transaccionesRepository.findByReferencia(referencia)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado con la referencia: " + referencia));
    }
}
