package com.project.minimercado.services.bussines;

import com.project.minimercado.repository.bussines.ProductosRepository;
import com.project.minimercado.repository.bussines.TransaccionesRepository;
import com.project.minimercado.repository.bussines.VentaRepository;
import org.springframework.stereotype.Service;

@Service
public class VentaService {
    private  VentaRepository ventaRepository;
    private  ProductosRepository productoRepository;
    private  TransaccionesRepository transaccionesRepository;
    public VentaService(VentaRepository ventaRepository, ProductosRepository productoRepository, TransaccionesRepository transaccionesRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.transaccionesRepository = transaccionesRepository;
    }



}
