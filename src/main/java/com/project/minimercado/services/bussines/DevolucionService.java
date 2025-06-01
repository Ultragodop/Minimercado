package com.project.minimercado.services.bussines;

import com.project.minimercado.model.bussines.*;
import com.project.minimercado.repository.bussines.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class DevolucionService {

    private final DevolucionRepository devolucionRepository;
    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;

    @Autowired
    public DevolucionService(
        DevolucionRepository devolucionRepository,
        VentaRepository ventaRepository,
        ProductoRepository productoRepository
    ) {
        this.devolucionRepository = devolucionRepository;
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional
    public Devolucion crearDevolucion(Devolucion devolucion) {
        // Validar que la venta existe
        Venta venta = ventaRepository.findById(devolucion.getVenta().getId())
            .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        // Validar que la venta es en efectivo
        if (!"EFECTIVO".equalsIgnoreCase(venta.getTipoPago())) {
            throw new RuntimeException("Solo se permiten devoluciones de ventas en efectivo");
        }

        // Validar que la venta no tiene más de 24 horas
        long horasTranscurridas = ChronoUnit.HOURS.between(venta.getFecha(), Instant.now());
        if (horasTranscurridas > 24) {
            throw new RuntimeException("No se pueden realizar devoluciones de ventas con más de 24 horas de antigüedad");
        }

        // Validar que la venta no tiene devoluciones previas
        if (!devolucionRepository.findByVentaId(venta.getId()).isEmpty()) {
            throw new RuntimeException("La venta ya tiene una devolución registrada");
        }

        // Calcular el total de la devolución
        BigDecimal total = devolucion.getDetallesDevolucion().stream()
            .map(detalle -> detalle.getPrecioUnitario().multiply(new BigDecimal(detalle.getCantidad())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        devolucion.setTotal(total);
        devolucion.setEstado(EstadoDevolucion.PENDIENTE);
        devolucion.setFecha(Instant.now());

        return devolucionRepository.save(devolucion);
    }

    @Transactional
    public Devolucion aprobarDevolucion(Integer idDevolucion, String usuarioAprobacion, String comentario) {
        Devolucion devolucion = devolucionRepository.findById(idDevolucion)
            .orElseThrow(() -> new RuntimeException("Devolución no encontrada"));

        if (devolucion.getEstado() != EstadoDevolucion.PENDIENTE) {
            throw new RuntimeException("La devolución no está en estado pendiente");
        }

        // Actualizar inventario (stock de productos)
        for (DetalleDevolucion detalle : devolucion.getDetallesDevolucion()) {
            Producto producto = productoRepository.findById(detalle.getProducto().getId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            // Actualizar el stock del producto
            producto.setStockActual(producto.getStockActual() + detalle.getCantidad());
            productoRepository.save(producto);
        }

        // Actualizar estado de la devolución
        devolucion.setEstado(EstadoDevolucion.APROBADA);
        devolucion.setFechaAprobacion(Instant.now());
        devolucion.setUsuarioAprobacion(usuarioAprobacion);
        devolucion.setComentarioAprobacion(comentario);

        return devolucionRepository.save(devolucion);
    }

    @Transactional
    public Devolucion rechazarDevolucion(Integer idDevolucion, String usuarioAprobacion, String comentario) {
        Devolucion devolucion = devolucionRepository.findById(idDevolucion)
            .orElseThrow(() -> new RuntimeException("Devolución no encontrada"));

        if (devolucion.getEstado() != EstadoDevolucion.PENDIENTE) {
            throw new RuntimeException("La devolución no está en estado pendiente");
        }

        devolucion.setEstado(EstadoDevolucion.RECHAZADA);
        devolucion.setFechaAprobacion(Instant.now());
        devolucion.setUsuarioAprobacion(usuarioAprobacion);
        devolucion.setComentarioAprobacion(comentario);

        return devolucionRepository.save(devolucion);
    }

    public List<Devolucion> obtenerDevolucionesPendientes() {
        return devolucionRepository.findByEstado(EstadoDevolucion.PENDIENTE);
    }

    public List<Devolucion> obtenerDevolucionesPorFecha(Instant fechaInicio, Instant fechaFin) {
        return devolucionRepository.findByFechaBetween(fechaInicio, fechaFin);
    }

    public List<Devolucion> obtenerDevolucionesPorVenta(Integer idVenta) {
        return devolucionRepository.findByVentaId(idVenta);
    }

    public List<Devolucion> obtenerDevolucionesPorUsuario(Integer idUsuario) {
        return devolucionRepository.findByUsuarioId(idUsuario);
    }

    public Optional<Devolucion> obtenerDevolucionPorId(Integer id) {
        return devolucionRepository.findById(id);
    }
} 