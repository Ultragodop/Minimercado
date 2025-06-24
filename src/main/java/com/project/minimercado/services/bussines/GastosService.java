package com.project.minimercado.services.bussines;

import com.project.minimercado.model.bussines.Gasto;
import com.project.minimercado.model.bussines.MovimientosContable;
import com.project.minimercado.repository.bussines.GastoRepository;
import com.project.minimercado.repository.bussines.TransaccionesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class GastosService {
    private final GastoRepository gastosRepository;
    private final TransaccionesRepository transaccionesRepository;

    public GastosService(GastoRepository gastosRepository,
                         TransaccionesRepository transaccionesRepository) {
        this.gastosRepository = gastosRepository;
        this.transaccionesRepository = transaccionesRepository;
    }

    @Transactional
    public Gasto registrarGasto(Gasto gasto) {
        validarGasto(gasto);

        // Establecer la fecha si no está establecida
        if (gasto.getFecha() == null) {
            gasto.setFecha(LocalDate.now());
        }

        // Guardar el gasto
        Gasto gastoGuardado = gastosRepository.save(gasto);

        // Registrar el movimiento contable
        MovimientosContable movimiento = new MovimientosContable();
        movimiento.setFecha(Instant.now());
        movimiento.setTipo("EGRESO");
        movimiento.setDescripcion("Gasto: " + gasto.getDescripcion() + " - Categoría: " + gasto.getCategoriaGasto());
        movimiento.setMonto(gasto.getMonto());
        movimiento.setReferencia("GASTO-" + gastoGuardado.getId());

        transaccionesRepository.save(movimiento);

        return gastoGuardado;
    }

    @Transactional
    public void actualizarGasto(Integer id, Gasto gastoActualizado) {
        Gasto gastoExistente = gastosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gasto no encontrado"));

        validarGasto(gastoActualizado);

        // Actualizar los campos del gasto
        gastoExistente.setDescripcion(gastoActualizado.getDescripcion());
        gastoExistente.setMonto(gastoActualizado.getMonto());
        gastoExistente.setFecha(gastoActualizado.getFecha());
        gastoExistente.setCategoriaGasto(gastoActualizado.getCategoriaGasto());

        // Guardar el gasto actualizado
        gastosRepository.save(gastoExistente);

        // Actualizar el movimiento contable asociado
        Optional<MovimientosContable> movimientoOptional =
                transaccionesRepository.findByReferencia("GASTO-" + id);

        if (movimientoOptional.isPresent()) {
            MovimientosContable movimiento = movimientoOptional.get();
            movimiento.setDescripcion("Gasto: " + gastoActualizado.getDescripcion() +
                    " - Categoría: " + gastoActualizado.getCategoriaGasto());
            movimiento.setMonto(gastoActualizado.getMonto());
            transaccionesRepository.save(movimiento);
        }
    }

    @Transactional(readOnly = true)
    public List<Gasto> obtenerGastosPorFecha(LocalDate fecha) {
        return gastosRepository.findByFecha(fecha);
    }

    @Transactional(readOnly = true)
    public List<Gasto> obtenerGastosPorCategoria(String categoria) {
        return gastosRepository.findByCategoriaGasto(categoria);
    }

    @Transactional(readOnly = true)
    public List<Gasto> obtenerGastosEntreFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return gastosRepository.findByFechaBetween(fechaInicio, fechaFin);
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularTotalGastosPorCategoria(String categoria) {
        return gastosRepository.findByCategoriaGasto(categoria).stream()
                .map(Gasto::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularTotalGastosEntreFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return gastosRepository.findByFechaBetween(fechaInicio, fechaFin).stream()
                .map(Gasto::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public void eliminarGasto(Integer id) {
        Gasto gasto = gastosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gasto no encontrado"));

        // Eliminar el movimiento contable asociado
        Optional<MovimientosContable> movimiento =
                transaccionesRepository.findByReferencia("GASTO-" + id);

        movimiento.ifPresent(transaccionesRepository::delete);

        // Eliminar el gasto
        gastosRepository.delete(gasto);
    }

    private void validarGasto(Gasto gasto) {
        if (gasto == null) {
            throw new RuntimeException("El gasto no puede ser nulo");
        }
        if (gasto.getMonto() == null || gasto.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El monto debe ser mayor que cero");
        }
        if (gasto.getCategoriaGasto() == null || gasto.getCategoriaGasto().trim().isEmpty()) {
            throw new RuntimeException("La categoría del gasto es requerida");
        }
        if (gasto.getDescripcion() == null || gasto.getDescripcion().trim().isEmpty()) {
            throw new RuntimeException("La descripción del gasto es requerida");
        }
    }
}
