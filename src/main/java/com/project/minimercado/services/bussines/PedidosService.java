package com.project.minimercado.services.bussines;

import com.project.minimercado.model.bussines.DetallePedidoProveedor;
import com.project.minimercado.model.bussines.PedidosProveedor;
import com.project.minimercado.model.bussines.Producto;
import com.project.minimercado.model.bussines.Proveedores;
import com.project.minimercado.repository.bussines.DetallePedidoProveedorRepository;
import com.project.minimercado.repository.bussines.PedidosProveedorRepository;
import com.project.minimercado.repository.bussines.ProductosRepository;
import com.project.minimercado.repository.bussines.ProveedoresRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PedidosService {


    private final PedidosProveedorRepository pedidosRepository;


    private final DetallePedidoProveedorRepository detalleRepository;


    private final ProveedoresRepository proveedoresRepository;


    private final ProductosRepository productosRepository;

    public PedidosService(PedidosProveedorRepository pedidosRepository,
                          DetallePedidoProveedorRepository detalleRepository,
                          ProveedoresRepository proveedoresRepository,
                          ProductosRepository productosRepository) {
        this.pedidosRepository = pedidosRepository;
        this.detalleRepository = detalleRepository;
        this.proveedoresRepository = proveedoresRepository;
        this.productosRepository = productosRepository;
    }

    public List<PedidosProveedor> getAllPedidos() {
        return pedidosRepository.findAll();
    }

    public Optional<PedidosProveedor> getPedidoById(Integer id) {
        return pedidosRepository.findById(id);
    }

    @Transactional
    public PedidosProveedor createPedido(PedidosProveedor pedido) {
        validatePedido(pedido);
        pedido.setFechaPedido(LocalDate.now());
        pedido.setEstado("pendiente");
        return pedidosRepository.save(pedido);
    }

    @Transactional
    public PedidosProveedor updatePedido(Integer id, PedidosProveedor pedidoActualizado) {
        Optional<PedidosProveedor> pedidoExistente = pedidosRepository.findById(id);
        if (pedidoExistente.isPresent()) {
            PedidosProveedor pedido = pedidoExistente.get();

            // Solo permitir actualizar ciertos campos
            pedido.setFechaEntrega(pedidoActualizado.getFechaEntrega());
            pedido.setEstado(pedidoActualizado.getEstado());

            // Si el estado cambia a "entregado", actualizar el stock de productos
            if ("entregado".equals(pedidoActualizado.getEstado()) && !"entregado".equals(pedido.getEstado())) {
                actualizarStockProductos(pedido);
            }

            return pedidosRepository.save(pedido);
        }
        throw new RuntimeException("Pedido no encontrado con ID: " + id);
    }

    @Transactional
    public void deletePedido(Integer id) {
        Optional<PedidosProveedor> pedido = pedidosRepository.findById(id);
        if (pedido.isPresent()) {
            // Solo permitir eliminar pedidos en estado "pendiente"
            if ("pendiente".equals(pedido.get().getEstado())) {
                pedidosRepository.deleteById(id);
            } else {
                throw new RuntimeException("No se puede eliminar un pedido que no está en estado pendiente");
            }
        } else {
            throw new RuntimeException("Pedido no encontrado con ID: " + id);
        }
    }

    @Transactional
    public PedidosProveedor agregarDetallePedido(Integer pedidoId, DetallePedidoProveedor detalle) {
        Optional<PedidosProveedor> pedidoOpt = pedidosRepository.findById(pedidoId);
        if (pedidoOpt.isPresent()) {
            PedidosProveedor pedido = pedidoOpt.get();

            // Validar que el pedido no esté entregado
            if ("entregado".equals(pedido.getEstado())) {
                throw new RuntimeException("No se pueden agregar detalles a un pedido ya entregado");
            }

            // Validar el producto
            validateProducto(detalle.getIdProducto());

            // Agregar el detalle al pedido
            detalle.setIdPedido(pedido);
            detalleRepository.save(detalle);
            pedido.getDetallePedidoProveedors().add(detalle);

            return pedidosRepository.save(pedido);
        }
        throw new RuntimeException("Pedido no encontrado con ID: " + pedidoId);
    }

    private void validatePedido(PedidosProveedor pedido) {
        if (pedido.getIdProveedor() == null) {
            throw new RuntimeException("El proveedor es requerido");
        }

        // Validar que el proveedor exista
        if (!proveedoresRepository.existsById(pedido.getIdProveedor().getId())) {
            throw new RuntimeException("El proveedor no existe");
        }

        // Validar los detalles del pedido
        Set<DetallePedidoProveedor> detalles = pedido.getDetallePedidoProveedors();
        if (detalles == null || detalles.isEmpty()) {
            throw new RuntimeException("El pedido debe tener al menos un detalle");
        }

        for (DetallePedidoProveedor detalle : detalles) {
            validateDetallePedido(detalle);
        }
    }

    private void validateDetallePedido(DetallePedidoProveedor detalle) {
        if (detalle.getIdProducto() == null) {
            throw new RuntimeException("El producto es requerido en el detalle");
        }

        if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
            throw new RuntimeException("La cantidad debe ser mayor a 0");
        }

        if (detalle.getPrecioUnitario() == null || detalle.getPrecioUnitario().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El precio unitario debe ser mayor a 0");
        }

        validateProducto(detalle.getIdProducto());
    }

    private void validateProducto(Producto producto) {
        if (producto == null || producto.getId() == null) {
            throw new RuntimeException("El producto es requerido");
        }

        if (!productosRepository.existsById(producto.getId())) {
            throw new RuntimeException("El producto no existe");
        }
    }

    private void actualizarStockProductos(PedidosProveedor pedido) {
        for (DetallePedidoProveedor detalle : pedido.getDetallePedidoProveedors()) {
            Producto producto = detalle.getIdProducto();
            producto.setStockActual(producto.getStockActual() + detalle.getCantidad());
            productosRepository.save(producto);
        }
    }

    public List<PedidosProveedor> getPedidosByProveedor(Integer proveedorId) {
        Optional<Proveedores> proveedor = proveedoresRepository.findById(proveedorId);
        if (proveedor.isPresent()) {
            return pedidosRepository.findAll().stream()
                    .filter(pedido -> pedido.getIdProveedor().getId().equals(proveedorId))
                    .toList();
        }
        throw new RuntimeException("Proveedor no encontrado con ID: " + proveedorId);
    }

    public List<PedidosProveedor> getPedidosByEstado(String estado) {
        return pedidosRepository.findAll().stream()
                .filter(pedido -> pedido.getEstado().equals(estado))
                .toList();
    }
}
