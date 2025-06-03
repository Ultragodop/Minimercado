package com.project.minimercado.repository.bussines;

import com.project.minimercado.dto.bussines.Inventario.CategoriaProductoDTO;
import com.project.minimercado.model.bussines.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoriasRepository extends JpaRepository<Categoria, Integer> {
    @Query("SELECT c.id AS id, c.nombre AS nombre, " +
            "(SELECT p.nombre FROM Producto p WHERE p.idCategoria = c) AS productosNombres " +
            "FROM Categoria c")
    List<CategoriaProductoDTO> findCategoriasConProductos();

}
