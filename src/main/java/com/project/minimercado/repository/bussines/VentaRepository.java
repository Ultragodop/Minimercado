package com.project.minimercado.repository.bussines;


import com.project.minimercado.model.bussines.Venta;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VentaRepository extends JpaRepository<Venta, Integer> {

    Optional<Venta> findByTransactionExternalId(String transactionExternalId);

    @NotNull Optional<Venta> findById(@NotNull Integer id);


}
