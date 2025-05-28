package com.project.minimercado.services.bussines;

import com.project.minimercado.model.bussines.*;
import com.project.minimercado.repository.bussines.ProductosRepository;
import com.project.minimercado.repository.bussines.TransaccionesRepository;
import com.project.minimercado.repository.bussines.VentaRepository;
import com.project.minimercado.repository.bussines.DetalleVentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
public class VentaService {
    private final VentaRepository ventaRepository;
    private final ProductosRepository productoRepository;
    private final TransaccionesRepository transaccionesRepository;
    private final DetalleVentaRepository detalleVentaRepository;

    public VentaService(VentaRepository ventaRepository, 
                       ProductosRepository productoRepository, 
                       TransaccionesRepository transaccionesRepository,
                       DetalleVentaRepository detalleVentaRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.transaccionesRepository = transaccionesRepository;
        this.detalleVentaRepository = detalleVentaRepository;
    }

    @Transactional
    public Venta realizarVentaEfectivo(Usuario usuario, List<DetalleVentaTemp> detallesVenta) {
        // Validaciones iniciales
        if (usuario == null) {
            throw new RuntimeException("El usuario es requerido para realizar la venta");
        }
        if (detallesVenta == null || detallesVenta.isEmpty()) {
            throw new RuntimeException("No hay productos en la venta");
        }

        // Crear la venta
        Venta venta = new Venta();
        venta.setFecha(Instant.now());
        venta.setIdUsuario(usuario);
        venta.setTipoPago("EFECTIVO");
        BigDecimal totalVenta = BigDecimal.ZERO;

        // Procesar cada detalle de venta
        Set<DetalleVenta> detalles = venta.getDetalleVentas();
        
        for (DetalleVentaTemp det : detallesVenta) {
            Producto producto = productoRepository.findById(det.getIdProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + det.getIdProducto()));

            // Validar stock
            if (producto.getStockActual() < det.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            // Crear detalle de venta
            DetalleVenta detalle = new DetalleVenta();
            detalle.setIdProducto(producto);
            detalle.setIdVenta(venta);
            detalle.setCantidad(det.getCantidad());
            detalle.setPrecioUnitario(BigDecimal.valueOf(producto.getPrecioVenta()));
            detalle.setSubtotal(detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(det.getCantidad())));
            
            // Actualizar stock
            producto.setStockActual(producto.getStockActual() - det.getCantidad());
            productoRepository.save(producto);

            // Agregar al total
            totalVenta = totalVenta.add(detalle.getSubtotal());
            detalles.add(detalle);
        }

        // Establecer el total de la venta
        venta.setTotal(totalVenta);
        
        // Guardar la venta
        venta = ventaRepository.save(venta);

        // Registrar el movimiento contable
        registrarMovimientoContable(venta);

        return venta;
    }

    private void registrarMovimientoContable(Venta venta) {
        MovimientosContable movimiento = new MovimientosContable();
        movimiento.setFecha(venta.getFecha());
        movimiento.setTipo("INGRESO");
        movimiento.setDescripcion("Venta en efectivo #" + venta.getId());
        movimiento.setMonto(venta.getTotal());
        movimiento.setReferencia("VENTA-" + venta.getId());
        
        transaccionesRepository.save(movimiento);
    }

    @Transactional(readOnly = true)
    public Venta obtenerVenta(Integer idVenta) {
        return ventaRepository.findById(idVenta)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
    }

    // Clase auxiliar para manejar los detalles de venta temporales
    public static class DetalleVentaTemp {
        private Integer idProducto;
        private Integer cantidad;

        public Integer getIdProducto() {
            return idProducto;
        }

        public void setIdProducto(Integer idProducto) {
            this.idProducto = idProducto;
        }

        public Integer getCantidad() {
            return cantidad;
        }

        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
        }
    }
}
