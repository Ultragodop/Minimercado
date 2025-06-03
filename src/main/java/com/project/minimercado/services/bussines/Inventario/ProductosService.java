package com.project.minimercado.services.bussines.Inventario;

import com.project.minimercado.dto.bussines.Inventario.ProductoDTO;
import com.project.minimercado.model.bussines.Categoria;
import com.project.minimercado.model.bussines.Producto;
import com.project.minimercado.repository.bussines.ProductosRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductosService {
    private final ProductosRepository productosRepository;

    public ProductosService(ProductosRepository productosRepository) {
        this.productosRepository = productosRepository;
    }

    @Transactional
    public Producto crearProducto(Producto producto) {
        validateproduct(producto);
        return productosRepository.save(producto);
    }
    public List<ProductoDTO> obtenerProductos() {

       try {
           return productosRepository.findAllProductoDTOs();
       }catch (Exception e) {
            throw new UnsupportedOperationException("No se puede obtener un producto");

        }
    }

    @Transactional
    public Producto actualizarProducto(Integer id, Producto productoActualizado) {
        Producto productoExistente = obtenerProductoPorId(id);
        
        // Actualizar campos
        productoExistente.setNombre(productoActualizado.getNombre());
        productoExistente.setDescripcion(productoActualizado.getDescripcion());
        productoExistente.setPrecioCompra(productoActualizado.getPrecioCompra());
        productoExistente.setPrecioVenta(productoActualizado.getPrecioVenta());
        productoExistente.setIdCategoria(productoActualizado.getIdCategoria());
        productoExistente.setStockActual(productoActualizado.getStockActual());
        productoExistente.setStockMinimo(productoActualizado.getStockMinimo());
        productoExistente.setIdProveedor(productoActualizado.getIdProveedor());
        productoExistente.setActivo(productoActualizado.getActivo());

        validateproduct(productoExistente);
        return productosRepository.save(productoExistente);
    }

    @Transactional
    public void eliminarProducto(Integer id) {
        Producto producto = obtenerProductoPorId(id);
        producto.setActivo(false);
        productosRepository.save(producto);
    }

    @Transactional(readOnly = true)
    public Producto obtenerProductoPorId(Integer id) {
        return productosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Producto> listarProductosActivos() {
        return productosRepository.findAll().stream()
                .filter(Producto::getActivo)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Producto> buscarProductosPorCategoria(Categoria categoria) {
        return productosRepository.findProductoByIdCategoria(categoria).stream()
                .filter(Producto::getActivo)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Producto> obtenerProductosBajoStock() {
        return productosRepository.findAll().stream()
                .filter(p -> p.getActivo() && p.getStockActual() <= p.getStockMinimo())
                .toList();
    }

    @Transactional
    public Producto actualizarStock(Integer id, Integer cantidad) {
        Producto producto = obtenerProductoPorId(id);
        producto.setStockActual(producto.getStockActual() + cantidad);
        return productosRepository.save(producto);
    }

    public Producto validateproduct(Producto producto) {
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre del producto es requerido");
        }
        if (producto.getPrecioCompra() == null || producto.getPrecioCompra() <= 0) {
            throw new RuntimeException("El precio de compra debe ser mayor a 0");
        }
        if (producto.getPrecioVenta() == null || producto.getPrecioVenta() <= 0) {
            throw new RuntimeException("El precio de venta debe ser mayor a 0");
        }
        if (producto.getPrecioVenta() < producto.getPrecioCompra()) {
            throw new RuntimeException("El precio de venta no puede ser menor al precio de compra");
        }
        if (producto.getIdCategoria() == null) {
            throw new RuntimeException("La categoría es requerida");
        }
        if (producto.getIdProveedor() == null) {
            throw new RuntimeException("El proveedor es requerido");
        }
        if (producto.getStockActual() == null || producto.getStockActual() < 0) {
            throw new RuntimeException("El stock actual no puede ser negativo");
        }
        if (producto.getStockMinimo() == null || producto.getStockMinimo() < 0) {
            throw new RuntimeException("El stock mínimo no puede ser negativo");
        }
        return producto;
    }
}

