package com.project.minimercado.services.bussines;

import com.project.minimercado.model.bussines.*;
import com.project.minimercado.repository.bussines.ProductosRepository;
import com.project.minimercado.repository.bussines.TransaccionesRepository;
import com.project.minimercado.repository.bussines.VentaRepository;
import com.project.minimercado.repository.bussines.DetalleVentaRepository;
import com.project.minimercado.dto.payment.Product;
import com.project.minimercado.dto.payment.PaymentRequest;
import com.project.minimercado.dto.payment.Cart;
import com.project.minimercado.dto.payment.Client;
import com.project.minimercado.services.payment.PaymentService;
import com.project.minimercado.config.PaymentConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.ArrayList;

@Service
public class VentaService {
    private final VentaRepository ventaRepository;
    private final ProductosRepository productoRepository;
    private final TransaccionesRepository transaccionesRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final PaymentService paymentService;
    private final PaymentConfig paymentConfig;

    public VentaService(VentaRepository ventaRepository, 
                       ProductosRepository productoRepository, 
                       TransaccionesRepository transaccionesRepository,
                       DetalleVentaRepository detalleVentaRepository,
                       PaymentService paymentService,
                       PaymentConfig paymentConfig) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.transaccionesRepository = transaccionesRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.paymentService = paymentService;
        this.paymentConfig = paymentConfig;
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

    @Transactional
    public String realizarVentaTarjeta(Usuario usuario, List<DetalleVentaTemp> detallesVenta) {
        // Validaciones iniciales
        validarVenta(usuario, detallesVenta);
        validarPermisosUsuario(usuario);

        // Crear la venta
        Venta venta = crearVentaInicial(usuario, "TARJETA");
        venta.setEstado("PENDIENTE_PAGO");
        BigDecimal totalVenta = BigDecimal.ZERO;
        List<Product> paymentProducts = new ArrayList<>();

        // Procesar detalles de venta
        for (DetalleVentaTemp det : detallesVenta) {
            Producto producto = obtenerYValidarProducto(det.getIdProducto());
            
            // Validar stock antes de crear la venta
            if (producto.getStockActual() < det.getCantidad()) {
                throw new IllegalArgumentException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            DetalleVenta detalle = crearDetalleVenta(venta, producto, det.getCantidad());
            totalVenta = totalVenta.add(detalle.getSubtotal());
            
            paymentProducts.add(crearProductoPago(detalle));
        }

        // Configurar venta
        venta.setTotal(totalVenta);

        try {
            // Crear y enviar solicitud de pago
            String paymentUrl = crearSolicitudPago(venta, paymentProducts, totalVenta);

            // Guardar venta
            venta = ventaRepository.save(venta);

            return paymentUrl;
        } catch (Exception e) {
            // Si hay error en el proceso de pago, la transacción se revierte automáticamente
            throw new RuntimeException("Error al procesar el pago: " + e.getMessage(), e);
        }
    }

    private void validarVenta(Usuario usuario, List<DetalleVentaTemp> detallesVenta) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario es requerido para realizar la venta");
        }
        if (detallesVenta == null || detallesVenta.isEmpty()) {
            throw new IllegalArgumentException("No hay productos en la venta");
        }
    }

    private void validarPermisosUsuario(Usuario usuario) {
        if (!"ADMIN".equals(usuario.getRol()) && !"VENDEDOR".equals(usuario.getRol())) {
            throw new IllegalArgumentException("El usuario no tiene permisos para realizar ventas con tarjeta");
        }
    }

    private Venta crearVentaInicial(Usuario usuario, String tipoPago) {
        Venta venta = new Venta();
        venta.setFecha(Instant.now());
        venta.setIdUsuario(usuario);
        venta.setTipoPago(tipoPago);
        return venta;
    }

    private Producto obtenerYValidarProducto(Integer idProducto) {
        Producto producto = productoRepository.findById(idProducto)
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + idProducto));
        
        if (producto.getStockActual() <= 0) {
            throw new IllegalArgumentException("Stock insuficiente para el producto: " + producto.getNombre());
        }
        
        return producto;
    }

    private DetalleVenta crearDetalleVenta(Venta venta, Producto producto, Integer cantidad) {
        DetalleVenta detalle = new DetalleVenta();
        detalle.setIdProducto(producto);
        detalle.setIdVenta(venta);
        detalle.setCantidad(cantidad);
        detalle.setPrecioUnitario(BigDecimal.valueOf(producto.getPrecioVenta()));
        detalle.setSubtotal(detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(cantidad)));
        return detalle;
    }

    private Product crearProductoPago(DetalleVenta detalle) {
        Product paymentProduct = new Product();
        paymentProduct.setName(detalle.getIdProducto().getNombre());
        paymentProduct.setQuantity(BigDecimal.valueOf(detalle.getCantidad()));
        paymentProduct.setAmount(detalle.getSubtotal());
        paymentProduct.setTaxedAmount(detalle.getSubtotal().multiply(BigDecimal.valueOf(0.82)));
        return paymentProduct;
    }

    private String crearSolicitudPago(Venta venta, List<Product> paymentProducts, BigDecimal totalVenta) {
        PaymentRequest paymentRequest = new PaymentRequest();
        
        String transactionId = UUID.randomUUID().toString();
        
        Cart cart = new Cart();
        cart.setInvoiceNumber(venta.getId());
        cart.setTotalAmount(totalVenta);
        cart.setTaxedAmount(totalVenta.multiply(BigDecimal.valueOf(0.82)));
        cart.setProducts(paymentProducts);
        cart.setTransactionExternalId(transactionId);
        
        Client client = new Client();
        client.setCommerceName(paymentConfig.getCommerceName());
        client.setSiteUrl(paymentConfig.getSiteUrl());
        
        paymentRequest.setCart(cart);
        paymentRequest.setClient(client);
        paymentRequest.setCallbackUrl(paymentConfig.getCallbackUrl());

        // Guardar el transactionId en la venta
        venta.setTransactionExternalId(transactionId);

        return paymentService.createPayment(paymentRequest);
    }

    @Transactional
    public void procesarCallbackPago(String transactionExternalId, Integer status) {
        Venta venta = ventaRepository.findByTransactionExternalId(transactionExternalId)
            .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));

        if (status == 1) { // Success
            procesarVentaExitosa(venta);
        } else {
            venta.setEstado("RECHAZADA");
            ventaRepository.save(venta);
        }
    }

    private void procesarVentaExitosa(Venta venta) {
        // Actualizar stock
        for (DetalleVenta detalle : venta.getDetalleVentas()) {
            Producto producto = detalle.getIdProducto();
            producto.setStockActual(producto.getStockActual() - detalle.getCantidad());
            productoRepository.save(producto);
        }
        
        venta.setEstado("COMPLETADA");
        ventaRepository.save(venta);
        
        registrarMovimientoContable(venta);
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
