package com.project.minimercado.dto.bussines.Analisis;



import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.Instant;
public record AnalisisProductoDTO(Integer id,
                                  Integer idProducto,
                                  String nombreProducto,
                                  Integer unidadesVendidas,
                                  BigDecimal ingresos,
                                  BigDecimal margenGanancia,
                                  Integer rotacion,
                                  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
                                  Instant fechaInicio,
                                  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
                                  Instant fechaFin,
                                  Integer stock,
                                  String periodo
) {
}
