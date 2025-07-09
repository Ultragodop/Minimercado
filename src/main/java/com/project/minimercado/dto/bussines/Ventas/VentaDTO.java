package com.project.minimercado.dto.bussines.Ventas;

import java.math.BigDecimal;
import java.time.Instant;

public interface VentaDTO {

    String getNombre();
    Instant getFecha();
    String getTipoPago();
    String getEstado();
    BigDecimal getTotal();


}
