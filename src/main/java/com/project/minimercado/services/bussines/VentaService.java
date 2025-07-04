package com.project.minimercado.services.bussines;

import com.project.minimercado.config.PaymentConfig;
import com.project.minimercado.dto.payment.Cart;
import com.project.minimercado.dto.payment.Client;
import com.project.minimercado.dto.payment.PaymentRequest;
import com.project.minimercado.dto.payment.Product;
import com.project.minimercado.model.bussines.*;
import com.project.minimercado.repository.bussines.*;
import com.project.minimercado.services.payment.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductosRepository productoRepository;
    private final TransaccionesRepository transaccionesRepository;
    private final PaymentService paymentService;
    private final PaymentConfig paymentConfig;
    private final UsuarioRepository usuarioRepository;

    public VentaService(VentaRepository ventaRepository,
                        ProductosRepository productoRepository,
                        TransaccionesRepository transaccionesRepository,
                        DetalleVentaRepository detalleVentaRepository,
                        PaymentService paymentService,
                        PaymentConfig paymentConfig, UsuarioRepository usuarioRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.transaccionesRepository = transaccionesRepository;
        this.paymentService = paymentService;
        this.paymentConfig = paymentConfig;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Venta realizarVentaEfectivo(Usuario idUsuario, List<DetalleVentaTemp> detallesVenta) {

        if (idUsuario == null) {
            throw new RuntimeException("El usuario es requerido para realizar la venta");
        }
        if (detallesVenta == null || detallesVenta.isEmpty()) {
            throw new RuntimeException("No hay productos en la venta");
        }

        Venta venta = new Venta();
        venta.setFecha(Instant.now());
        venta.setIdUsuario(idUsuario);
        venta.setTipoPago("EFECTIVO");
        venta.setEstado("PENDIENTE_PAGO");

        BigDecimal totalVenta = BigDecimal.ZERO;

        Set<DetalleVenta> detalles = venta.getDetalleVentas();

        for (DetalleVentaTemp det : detallesVenta) {
            Producto producto = productoRepository.findById(det.getIdProducto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + det.getIdProducto()));


            if (producto.getStockActual() < det.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
            }

      
            DetalleVenta detalle = new DetalleVenta();
            detalle.setIdProducto(producto);
            detalle.setIdVenta(venta);
            detalle.setCantidad(det.getCantidad());
            detalle.setPrecioUnitario(BigDecimal.valueOf(producto.getPrecioVenta()));
            detalle.setSubtotal(detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(det.getCantidad())));

            producto.setStockActual(producto.getStockActual() - det.getCantidad());
            productoRepository.save(producto);


            totalVenta = totalVenta.add(detalle.getSubtotal());
            detalles.add(detalle);
        }

        venta.setTotal(totalVenta);
        venta.setEstado("COMPLETADA");

        venta = ventaRepository.save(venta);


        registrarMovimientoContable(venta);

        return venta;
    }

    @Transactional
    public String realizarVentaTarjeta(Usuario idusuario, List<DetalleVentaTemp> detallesVenta) {

        validarVenta(idusuario, detallesVenta);
        validarPermisosUsuario(idusuario);


        Venta venta = crearVentaInicial(idusuario);
        venta.setEstado("PENDIENTE_PAGO");
        BigDecimal totalVenta = BigDecimal.ZERO;
        List<Product> paymentProducts = new ArrayList<>();

        for (DetalleVentaTemp det : detallesVenta) {
            Producto producto = obtenerYValidarProducto(det.getIdProducto());

            if (producto.getStockActual() < det.getCantidad()) {
                throw new IllegalArgumentException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            DetalleVenta detalle = crearDetalleVenta(venta, producto, det.getCantidad());
            totalVenta = totalVenta.add(detalle.getSubtotal());

            paymentProducts.add(crearProductoPago(detalle));
        }

        venta.setTotal(totalVenta);

        try {

            String paymentUrl = crearSolicitudPago(venta, paymentProducts, totalVenta);

            ventaRepository.save(venta);

            return paymentUrl;
        } catch (Exception e) {

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

    private Venta crearVentaInicial(Usuario usuario) {
        usuario.setId(usuario.getId());
        Venta venta = new Venta();
        venta.setFecha(Instant.now());
        venta.setIdUsuario(usuario);
        venta.setTipoPago("TARJETA");
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

        venta.setTransactionExternalId(transactionId);

        return paymentService.createPayment(paymentRequest);
    }

    @Transactional
    public void procesarCallbackPago(String transactionExternalId, Integer status) {
        Venta venta = ventaRepository.findByTransactionExternalId(transactionExternalId)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));

        if (status == 1) {
            procesarVentaExitosa(venta);
        } else {
            venta.setEstado("RECHAZADA");
            ventaRepository.save(venta);
        }
    }

    private void procesarVentaExitosa(Venta venta) {

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
