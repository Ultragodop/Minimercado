package com.project.minimercado.repository.bussines;

import com.project.minimercado.model.bussines.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    //List<Producto> findByActivoTrue();
    //List<Producto> findByStockLessThanStockMinimo();
    //List<Producto> findByFechaVencimientoBeforeAndActivoTrue(Instant fecha);
    List<Producto> findProductosByFechaVencimientoBeforeAndActivoTrue(Date fechaVencimiento);

    List<Producto> findProductosByStockActualLessThanAndStockMinimo(Integer stockActual, Integer stockMinimo);

    Optional<Producto> findById(Integer id);

    List<Producto> findProductosByActivo(Boolean activo);

    boolean existsById(Integer id);
} 