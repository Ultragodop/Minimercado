package com.project.minimercado.repository.bussines;

import com.project.minimercado.model.bussines.Categoria;
import com.project.minimercado.model.bussines.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductosRepository extends JpaRepository<Producto, Integer> {
    List<Producto> findProductoByIdCategoria(Categoria idCategoria);
}
