package com.project.minimercado.services.bussines;
import com.project.minimercado.model.bussines.Producto;
import com.project.minimercado.repository.bussines.ProductosRepository;
import org.springframework.stereotype.Service;


import java.util.List;
@Service
public class InventarioSevice {
    private ProductosRepository productosRepository;
    public InventarioSevice(ProductosRepository productosRepository) {
        this.productosRepository = productosRepository;
    }
    public void create(Producto producto) {

        // Validar que el producto no sea nulo
        if (producto == null) {
            throw new RuntimeException("El producto no puede ser nulo");
        }
        // Validar cantidad negativa
        if (producto.getStockActual() < 0) {
            throw new RuntimeException("La cantidad no puede ser negativa");
        }
        // Validar precio negativo
        if (producto.getPrecioCompra() < 0|| producto.getPrecioVenta() < 0) {
            throw new RuntimeException("El precio no puede ser negativo");
        }
        //Verificar que el producto tenga un código
        if (producto.getId() == null) {
            throw new RuntimeException("El producto debe tener un código");
        }
        // Verificar si el producto ya existe
        Boolean existingProductos = productosRepository.existsById(producto.getId());
        if (existingProductos.equals(true)) {
            throw new RuntimeException("El producto ya existe");
        }


        productosRepository.save(producto);

    }

    public void eliminarProducto(Producto producto) {
        // Verificar si el producto existe
        if (!productosRepository.existsById(producto.getId())) {

            throw new RuntimeException("El producto no existe");
        }
        // Verificar si el producto tiene stock
        if (producto.getStockActual() > 0) {
            throw new RuntimeException("El producto no se puede eliminar porque tiene stock");
        }
        // Eliminar el producto
        productosRepository.delete(producto);



    }

    public void actualizarProducto(Producto producto) {
        // Verificar si el producto existe
        if (!productosRepository.existsById(producto.getId())) {
            throw new RuntimeException("El producto no existe");
        }

        if (producto.getStockActual() < 0) {
            throw new RuntimeException("La cantidad no puede ser negativa");
        }
        // Validar precio negativo
        if (producto.getPrecioCompra() < 0 || producto.getPrecioVenta() < 0) {
            throw new RuntimeException("El precio no puede ser negativo");
        }

        // Verificar si el producto tiene stock
        if (producto.getStockActual() == 0) {
            throw new RuntimeException("El producto no puede tener stock negativo");
        }
        // Actualizar el producto
        productosRepository.save(producto);

    }

    public List<Producto> buscarProducto(Integer codigo) {
        // Verificar si el producto existe
       Boolean exists = productosRepository.existsById(codigo);
       if (exists.equals(true)) {
              return productosRepository.findById(codigo).stream().toList();
         } else {
              throw new RuntimeException("El producto no existe");
       }



    }
}
