package com.project.minimercado.controllers.bussines;

import com.project.minimercado.model.bussines.Categoria;
import com.project.minimercado.services.bussines.Inventario.CategoriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @PostMapping("/create")
    public ResponseEntity<Categoria> crearCategoria(@RequestBody Categoria categoria) {
        try {
            Categoria nuevaCategoria = categoriaService.crearCategoria(categoria);
            return new ResponseEntity<>(nuevaCategoria, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al crear la categoría: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> actualizarCategoria(@PathVariable Integer id, @RequestBody Categoria categoria) {
        try {
            Categoria categoriaActualizada = categoriaService.actualizarCategoria(id, categoria);
            return ResponseEntity.ok(categoriaActualizada);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al actualizar la categoría: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Integer id) {
        try {
            categoriaService.eliminarCategoria(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al eliminar la categoría: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> obtenerCategoriaPorId(@PathVariable Integer id) {
        try {
            Categoria categoria = categoriaService.obtenerCategoriaPorId(id);
            return ResponseEntity.ok(categoria);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al obtener la categoría: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Categoria>> listarCategorias() {
        try {
            List<Categoria> categorias = categoriaService.listarCategorias();
            return ResponseEntity.ok(categorias);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al listar las categorías: " + e.getMessage());
        }
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Categoria> buscarCategoriaPorNombre(@PathVariable String nombre) {
        try {
            Categoria categoria = categoriaService.buscarCategoriaPorNombre(nombre);
            return ResponseEntity.ok(categoria);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al buscar la categoría por nombre: " + e.getMessage());
        }
    }
} 