package com.project.minimercado.repository.bussines;

import com.project.minimercado.model.bussines.Producto;
import com.project.minimercado.model.bussines.Venta;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaRepository extends JpaRepository<Venta, Integer> {

    Optional<Venta> findByTransactionExternalId(String transactionExternalId);

    Optional<Venta> findById(Integer id);

}
