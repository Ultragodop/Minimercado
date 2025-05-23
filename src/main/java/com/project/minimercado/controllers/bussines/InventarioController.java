package com.project.minimercado.controllers.bussines;


import com.project.minimercado.model.bussines.Producto;
import com.project.minimercado.services.bussines.InventarioSevice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InventarioController {
    private final InventarioSevice inventarioSevice;
    public InventarioController(InventarioSevice inventarioSevice) {
        this.inventarioSevice = inventarioSevice;
    }
    @PostMapping("/create")
    public ResponseEntity agregarProducto(@RequestBody Producto producto) {

        inventarioSevice.create(producto);
        return ResponseEntity.ok(producto);

    }
}
