package com.project.minimercado.services.bussines;
import com.project.minimercado.model.bussines.Producto;
import com.project.minimercado.repository.bussines.ProductosRepository;


import java.util.List;

public class InventarioSevice {
    private ProductosRepository productosRepository;
    public InventarioSevice(ProductosRepository productosRepository) {
        this.productosRepository = productosRepository;
    }
    public void agregarProducto(Producto producto) {

    }

    public void eliminarProducto(Producto producto) {

    }

    public void actualizarProducto(Producto producto) {

    }

    public List<Producto> buscarProducto(String codigo) {

        return null;
    }
}
