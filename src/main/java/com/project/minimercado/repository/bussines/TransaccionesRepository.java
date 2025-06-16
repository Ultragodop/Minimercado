package com.project.minimercado.repository.bussines;

import com.project.minimercado.model.bussines.MovimientosContable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TransaccionesRepository extends JpaRepository<MovimientosContable, Integer> {

    List<MovimientosContable> findByFechaBetweenOrderByFechaDesc(Instant fechaInicio, Instant fechaFin);

    List<MovimientosContable> findByTipoOrderByFechaDesc(String tipo);

    Optional<MovimientosContable> findByReferencia(String referencia);

    @Query(value = "SELECT m FROM MovimientosContable m ORDER BY m.fecha DESC LIMIT :n")
    List<MovimientosContable> findTopNByOrderByFechaDesc(@Param("n") int n);
}
