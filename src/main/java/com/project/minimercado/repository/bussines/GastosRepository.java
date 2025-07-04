package com.project.minimercado.repository.bussines;

import com.project.minimercado.model.bussines.Gasto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface GastosRepository extends JpaRepository<Gasto, Integer> {
    List<Gasto> findByFecha(LocalDate fecha);

    List<Gasto> findByCategoriaGasto(String categoriaGasto);

    List<Gasto> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);
} 