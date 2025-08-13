package com.project.minimercado.services.bussines;

import com.project.minimercado.config.PaymentConfig;
import com.project.minimercado.dto.bussines.Ventas.*;
import com.project.minimercado.dto.payment.*;
import com.project.minimercado.model.bussines.*;
import com.project.minimercado.repository.bussines.*;
import com.project.minimercado.services.payment.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Slf4j
@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductosRepository productoRepository;
    private final TransaccionesRepository transaccionesRepository;
    private final PaymentService paymentService;
    private final PaymentConfig paymentConfig;
    private final UsuarioRepository usuarioRepository;
    private final DetalleVentaRepository detalleVentaRepository;

    public VentaService(VentaRepository ventaRepository,
                        ProductosRepository productoRepository,
                        TransaccionesRepository transaccionesRepository,
                        PaymentService paymentService,
                        PaymentConfig paymentConfig, UsuarioRepository usuarioRepository, DetalleVentaRepository detalleVentaRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.transaccionesRepository = transaccionesRepository;
        this.paymentService = paymentService;
        this.paymentConfig = paymentConfig;
        this.usuarioRepository = usuarioRepository;
        this.detalleVentaRepository = detalleVentaRepository;
    }

    @Transactional
    public VentaDTO realizarVentaEfectivo(Usuario idUsuario, List<DetalleVentaTemp> detallesVenta) {
        //Con un dto se podria modificar el precio de producto por venta, ejemplo: precio de venta con descuento, un precio elegido por el usuario a cargo(dependiendo del cliente que venga a comprar)
        if (idUsuario == null) {
            throw new RuntimeException("El usuario es requerido para realizar la venta");
        }
        if (detallesVenta == null || detallesVenta.isEmpty()) {
            throw new RuntimeException("No hay productos en la venta");
        }

        Venta venta = new Venta();
        venta.setFecha(Instant.now());
        venta.setIdUsuario(idUsuario);
        venta.setId(currentTimeMillis());
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


            detalle.setPrecioUnitario(BigDecimal.valueOf(producto.getPrecioVenta())); // Aca se puede modificar el precio de venta si se desea
            detalle.setSubtotal(detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(det.getCantidad())));
            //detalle.setPrecioUnitario(producto.getPrecioVenta()*det.getDescuento())
            producto.setStockActual(producto.getStockActual() - det.getCantidad());
            productoRepository.save(producto);


            totalVenta = totalVenta.add(detalle.getSubtotal());
            detalles.add(detalle);
        }

        venta.setTotal(totalVenta);
        venta.setEstado("COMPLETADA");

        venta = ventaRepository.save(venta);


        registrarMovimientoContable(venta);

        VentaDTO ventaDTO = ventaRepository.findVentaDTOById(venta.getId());

                if(ventaDTO == null) {
            throw new RuntimeException("Error al obtener la venta guardada");
                }
        System.out.println("Venta realizada con éxito: " + ventaDTO.getEstado());
        log.info("Venta realizada con éxito: {}", ventaDTO.getIdVenta());
        return ventaDTO;
    }

    @Transactional
    public String realizarVentaTarjeta(Usuario idusuario, List<DetalleVentaTemp> detallesVenta) {

        validarVenta(idusuario, detallesVenta);
        validarPermisosUsuario(idusuario);
        int idVenta= currentTimeMillis();
        Venta venta = crearVentaInicial(idusuario, idVenta);
        Set<DetalleVenta> detalles = venta.getDetalleVentas();

        if(ventaRepository.findById(idVenta).isPresent()) {
            throw new IllegalArgumentException("ID de venta ya existe, por favor intente nuevamente");
        }

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
            detalles.add(detalle);
            paymentProducts.add(crearProductoPago(detalle));

        }

        venta.setTotal(totalVenta);

        try {
          String transactionId = crearSolicitudPago(venta, paymentProducts, totalVenta);
            ventaRepository.save(venta);


            return transactionId;
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


    private Venta crearVentaInicial(Usuario usuario, int idVenta) {
        usuario.setId(usuario.getId());
        Venta venta = new Venta();
        venta.setId(idVenta);
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
        paymentProduct.setTaxedAmount(detalle.getSubtotal());
        System.out.println("Producto de pago creado: " + paymentProduct.getName() + ", Cantidad: " + paymentProduct.getQuantity() + ", Monto: " + paymentProduct.getAmount());
        return paymentProduct;
    }

    private String crearSolicitudPago(Venta venta, List<Product> paymentProducts, BigDecimal totalVenta) {
        PaymentRequest paymentRequest = new PaymentRequest();

        String transactionId = UUID.randomUUID().toString();

        Cart cart = new Cart();

        cart.setInvoiceNumber(venta.getId());
        cart.setTotalAmount(totalVenta);
        cart.setTaxedAmount(totalVenta);
        cart.setProducts(paymentProducts);
        cart.setTransactionExternalId(transactionId);

        Client client = new Client();
        client.setCommerceName(paymentConfig.getCommerceName());
        client.setSiteUrl(paymentConfig.getSiteUrl());

        paymentRequest.setCart(cart);
        paymentRequest.setClient(client);
        paymentRequest.setCallbackUrl(paymentConfig.getCallbackUrl());

        venta.setTransactionExternalId(transactionId);
        System.out.println("Solicitud de pago creada: " + paymentRequest.getCart().getInvoiceNumber() + ", Total: " + paymentRequest.getCart().getTotalAmount());
        String s = paymentService.createPayment(paymentRequest);
        System.out.println("Respuesta del servicio de pago: " + s);
                return transactionId;
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
public void validarCallBackPago(CallbackRequest request){

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

        @Transactional(readOnly = true) //TODO: Ver si se puede optimizar :D (me quiero matar)
        public List<VentaDTO> obtenerVentas() {
            List<VentaDetallePlanoDTO> planos = ventaRepository.findAllVentasDetallePlanos();

            Map<Integer, VentaDTOImpl> ventasMap = new LinkedHashMap<>();

            for (VentaDetallePlanoDTO fila : planos) {
                VentaDTOImpl venta = ventasMap.computeIfAbsent(
                        fila.getIdVenta(),
                        id -> new VentaDTOImpl(
                                fila.getIdVenta(),
                                fila.getNombre(),
                                fila.getFecha(),
                                fila.getTipoPago(),
                                fila.getEstado(),
                                fila.getTotal(),
                                new ArrayList<>()
                        )
                );

                venta.getDetalleVenta().add(
                        new DetalleVentaDTOImpl(
                                fila.getCantidad(),
                                fila.getPrecioUnitario(),
                                new ProductoDTOImpl(
                                        fila.getIdProducto(),
                                        fila.getNombreProducto()
                                )
                        )
                );
            }

            return new ArrayList<>(ventasMap.values());
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


    }


    public static Integer currentTimeMillis() {
        return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }
}
