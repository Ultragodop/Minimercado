package com.project.minimercado.controllers.bussines;

import com.project.minimercado.dto.bussines.Inventario.ProveedorDTO;
import com.project.minimercado.dto.bussines.Inventario.ProveedorProductosDTO;
import com.project.minimercado.model.bussines.Proveedores;
import com.project.minimercado.services.bussines.Inventario.ProveedorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {

    private final ProveedorService proveedorService;

    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> crearProveedor(@RequestBody Proveedores proveedor) {
        try {
            String nuevoProveedor = proveedorService.crearProveedor(proveedor);
            return new ResponseEntity<>(nuevoProveedor, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al crear el proveedor: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Proveedores> actualizarProveedor(@PathVariable Integer id, @RequestBody Proveedores proveedor) {
        try {
            Proveedores proveedorActualizado = proveedorService.actualizarProveedor(id, proveedor);
            return ResponseEntity.ok(proveedorActualizado);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al actualizar el proveedor: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProveedor(@PathVariable Integer id) {
        try {
            proveedorService.eliminarProveedor(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al eliminar el proveedor: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorProductosDTO> obtenerProveedorPorId(@PathVariable Integer id) {
        try {
            ProveedorProductosDTO proveedor = proveedorService.obtenerProveedorPorId(id);
            return ResponseEntity.ok(proveedor);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al obtener el proveedor: " + e.getMessage());
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<List<ProveedorDTO>> listarProveedores() {
        try {
            List<ProveedorDTO> proveedores = proveedorService.listarProveedoresDTO();
            return ResponseEntity.ok(proveedores);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al listar los proveedores: " + e.getMessage());
        }
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Proveedores> buscarProveedorPorNombre(@PathVariable String nombre) {
        try {
            Proveedores proveedor = proveedorService.buscarProveedorPorNombre(nombre);
            return ResponseEntity.ok(proveedor);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al buscar el proveedor por nombre: " + e.getMessage());
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<List<Proveedores>> buscarProveedoresPorEmail(@PathVariable String email) {
        try {
            List<Proveedores> proveedores = proveedorService.buscarProveedoresPorEmail(email);
            return ResponseEntity.ok(proveedores);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al buscar proveedores por email: " + e.getMessage());
        }
    }
}
