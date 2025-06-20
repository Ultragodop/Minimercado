package com.project.minimercado.repository.bussines;

import com.project.minimercado.dto.bussines.Inventario.ProveedorProductosDTO;
import com.project.minimercado.model.bussines.Proveedores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProveedoresRepository extends JpaRepository<Proveedores, Integer> {


    @Query("SELECT pr.id AS id, pr.nombre AS nombre, " +
            "(SELECT p.nombre FROM Producto p WHERE p.idProveedor = pr) AS productosNombres " +
            "FROM Proveedores pr")
    Optional<ProveedorProductosDTO> findProveedoresConProductos(Integer idProveedor);
}

