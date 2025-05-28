package com.project.minimercado.controllers.bussines;


import com.project.minimercado.model.bussines.Producto;
import com.project.minimercado.services.bussines.Inventario.InventarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InventarioController {
    private final InventarioService inventarioService;
    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }
    @PostMapping("/create")
    public ResponseEntity agregarProducto(@RequestBody Producto producto) {

        inventarioService.create(producto);
        return ResponseEntity.ok(producto);

    }
}
