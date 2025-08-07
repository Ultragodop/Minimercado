package com.project.minimercado.repository.bussines;

import com.project.minimercado.dto.bussines.Inventario.CategoriaDTO;
import com.project.minimercado.model.bussines.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoriasRepository extends JpaRepository<Categoria, Integer> {
    @Query("SELECT c.id AS id, c.nombre AS nombre, c.fechaCreacion AS fechaCreacion, c.descripcion as descripcion, c.activo as activo  FROM Categoria c ")
    List<CategoriaDTO> findCategoriasDTO();

}
