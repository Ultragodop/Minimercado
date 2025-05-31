package com.project.minimercado.repository.bussines;

import com.project.minimercado.model.bussines.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    List<Producto> findByActivoTrue();
    List<Producto> findByStockLessThanStockMinimo();
    List<Producto> findByFechaVencimientoBeforeAndActivoTrue(Instant fecha);
    List<Producto> findByCodigoBarras(String codigoBarras);
    boolean existsByCodigoBarras(String codigoBarras);
} 