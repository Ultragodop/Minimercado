package com.project.minimercado.services.bussines.Inventario;

import com.project.minimercado.model.bussines.Categoria;
import com.project.minimercado.model.bussines.Producto;
import com.project.minimercado.repository.bussines.ProductosRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {
    private final ProductosRepository productosRepository;

    public ProductoService(ProductosRepository productosRepository) {
        this.productosRepository = productosRepository;
    }

    @Transactional
    public void crearProducto(Producto producto) {
        validarProducto(producto);
        
        if (producto.getId() != null && productosRepository.existsById(producto.getId())) {
            throw new RuntimeException("El producto ya existe");
        }

        if (producto.getStockMinimo() == null) {
            producto.setStockMinimo(0);
        }
        if (producto.getActivo() == null) {
            producto.setActivo(true);
        }

        productosRepository.save(producto);
    }

    @Transactional
    public void desactivarProducto(Integer idProducto) {
        Producto producto = productosRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("El producto no existe"));

        if (producto.getStockActual() > 0) {
            throw new RuntimeException("El producto no se puede desactivar porque tiene stock");
        }

        producto.setActivo(false);
        productosRepository.save(producto);
    }

    @Transactional
    public void actualizarProducto(Producto producto) {
        validarProducto(producto);

        Producto productoExistente = productosRepository.findById(producto.getId())
                .orElseThrow(() -> new RuntimeException("El producto no existe"));

        productoExistente.setNombre(producto.getNombre());
        productoExistente.setDescripcion(producto.getDescripcion());
        productoExistente.setPrecioCompra(producto.getPrecioCompra());
        productoExistente.setPrecioVenta(producto.getPrecioVenta());
        productoExistente.setIdCategoria(producto.getIdCategoria());
        productoExistente.setStockMinimo(producto.getStockMinimo());
        productoExistente.setIdProveedor(producto.getIdProveedor());
        productoExistente.setStockActual(producto.getStockActual());
        productoExistente.setActivo(producto.getActivo());

        productosRepository.save(productoExistente);
    }

    @Transactional(readOnly = true)
    public Optional<Producto> buscarProductoPorId(Integer id) {
        return productosRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Producto> buscarProductosPorCategoria(Categoria categoria) {
        return productosRepository.findProductoByIdCategoria(categoria);
    }

    @Transactional(readOnly = true)
    public List<Producto> listarProductosActivos() {
        return productosRepository.findAll().stream()
                .filter(Producto::getActivo)
                .toList();
    }

    @Transactional
    public void ajustarStock(Integer idProducto, Integer cantidad) {
        Producto producto = productosRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("El producto no existe"));

        int nuevoStock = producto.getStockActual() + cantidad;
        if (nuevoStock < 0) {
            throw new RuntimeException("No hay suficiente stock disponible");
        }

        producto.setStockActual(nuevoStock);
        productosRepository.save(producto);
    }

    @Transactional(readOnly = true)
    public List<Producto> obtenerProductosBajoStock() {
        return productosRepository.findAll().stream()
                .filter(p -> p.getStockActual() <= p.getStockMinimo() && p.getActivo())
                .toList();
    }

    private void validarProducto(Producto producto) {
        if (producto == null) {
            throw new RuntimeException("El producto no puede ser nulo");
        }
        if (producto.getStockActual() == null || producto.getStockActual() < 0) {
            throw new RuntimeException("La cantidad no puede ser nula o negativa");
        }
        if (producto.getPrecioCompra() < 0 || producto.getPrecioVenta() < 0) {
            throw new RuntimeException("El precio no puede ser negativo");
        }
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre del producto es requerido");
        }
        if (producto.getIdCategoria() == null) {
            throw new RuntimeException("La categoría del producto es requerida");
        }
        if (producto.getIdProveedor() == null) {
            throw new RuntimeException("El proveedor del producto es requerido");
        }
    }
} 