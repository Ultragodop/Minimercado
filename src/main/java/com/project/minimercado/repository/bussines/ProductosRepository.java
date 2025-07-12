package com.project.minimercado.repository.bussines;

import com.project.minimercado.dto.bussines.Inventario.ProductoDTO;
import com.project.minimercado.model.bussines.Categoria;
import com.project.minimercado.model.bussines.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductosRepository extends JpaRepository<Producto, Integer> {


    List<Producto> findProductoByIdCategoria(Categoria idCategoria);

    List<Producto> findByNombre(String nombre);

    // Obtener productos con nombres de categoría y proveedor
    @Query("SELECT p.id AS id, p.nombre AS nombre, p.descripcion AS descripcion, " +
            "p.precioCompra AS precioCompra, p.precioVenta AS precioVenta, " +
            "p.fechaVencimiento AS fechaVencimiento, " +
            "p.idCategoria.nombre AS categoriaNombre, " + // Acceso directo al nombre
            "p.idProveedor.nombre AS proveedorNombre, " + // Acceso directo al nombre
            "p.stockActual AS stockActual, p.stockMinimo AS stockMinimo, " +
            "p.activo AS activo " +
            "FROM Producto p")
    List<ProductoDTO> findAllProductoDTOs();
    @Query("""
    SELECT
        p.id AS id,
        p.nombre AS nombre,
        p.descripcion AS descripcion,
        p.precioCompra AS precioCompra,
        p.precioVenta AS precioVenta,
        p.fechaVencimiento AS fechaVencimiento,
        p.idCategoria.nombre AS categoriaNombre,
        p.idProveedor.nombre AS proveedorNombre,
        p.stockActual AS stockActual,
        p.stockMinimo AS stockMinimo,
        p.activo AS activo
    FROM Producto p\s
    WHERE p.id = :id
""")
    ProductoDTO findProductoDTOById(@Param("id") Integer id);


}
