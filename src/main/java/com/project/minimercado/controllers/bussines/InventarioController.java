package com.project.minimercado.controllers.bussines;

import com.project.minimercado.dto.bussines.Inventario.ProductoDTO;
import com.project.minimercado.model.bussines.Producto;
import com.project.minimercado.services.bussines.Inventario.InventarioService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventario")
@Slf4j
public class InventarioController {
    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping("/estado")
    public ResponseEntity<Map<String, Object>> obtenerEstadoInventario() {
        try {
            return ResponseEntity.ok(inventarioService.obtenerEstadoInventario());
        } catch (Exception e) {
            log.error("Error al obtener estado del inventario", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/resumen/categoria")
    public ResponseEntity<Map<String, Object>> obtenerResumenPorCategoria() {
        try {
            return ResponseEntity.ok(inventarioService.obtenerResumenPorCategoria());
        } catch (Exception e) {
            log.error("Error al obtener resumen por categoría", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/resumen/proveedor")
    public ResponseEntity<Map<String, Object>> obtenerResumenPorProveedor() {
        try {
            return ResponseEntity.ok(inventarioService.obtenerResumenPorProveedor());
        } catch (Exception e) {
            log.error("Error al obtener resumen por proveedor", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/alertas")
    public ResponseEntity<List<Map<String, Object>>> obtenerAlertasInventario() {
        try {
            return ResponseEntity.ok(inventarioService.obtenerAlertasInventario());
        } catch (Exception e) {
            log.error("Error al obtener alertas de inventario", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticasInventario() {
        try {
            return ResponseEntity.ok(inventarioService.obtenerEstadisticasInventario());
        } catch (Exception e) {
            log.error("Error al obtener estadísticas de inventario", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(value = "/producto", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Producto> agregarProducto(@RequestBody Producto producto) {
        try {
            return ResponseEntity.ok(inventarioService.crearProducto(producto));
        } catch (IllegalArgumentException e) {
            log.warn("Error al agregar producto: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error al agregar producto", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/producto/{id}")
    public ResponseEntity<Producto> actualizarProducto(
            @PathVariable Integer id,
            @RequestBody Producto producto) {
        try {
            return ResponseEntity.ok(inventarioService.actualizarProducto(id, producto));
        } catch (IllegalArgumentException e) {
            log.warn("Error al actualizar producto: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error al actualizar producto", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/producto/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Integer id) {
        try {
            inventarioService.eliminarProducto(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.warn("Error al eliminar producto: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error al eliminar producto", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/producto/{id}")
    public ResponseEntity<ProductoDTO> obtenerProducto(@PathVariable Integer id) {
        try {
            ProductoDTO producto = inventarioService.obtenerProductoPorId(id);
            if (producto == null) {
                return null;
            }
            return ResponseEntity.ok(producto);
        } catch (IllegalArgumentException e) {
            log.warn("Error al obtener producto: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error al obtener producto", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/todos-productos")
    public ResponseEntity<List<ProductoDTO>> obtenerTodosLosProductos() {
        try {
            return ResponseEntity.ok(inventarioService.listarProductos());
        } catch (Exception e) {
            log.error("Error al obtener productos", e);
            return ResponseEntity.internalServerError().build();
        }

    }

    @GetMapping("/productos/activos")
    public ResponseEntity<List<ProductoDTO>> listarProductosActivos() {
        try {
            return ResponseEntity.ok(inventarioService.listarProductosActivos());
        } catch (Exception e) {
            log.error("Error al listar productos activos", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/productos/bajo-stock")
    public ResponseEntity<List<Producto>> obtenerProductosBajoStock() {
        try {
            return ResponseEntity.ok(inventarioService.obtenerProductosBajoStock());
        } catch (Exception e) {
            log.error("Error al obtener productos bajo stock", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/producto/{id}/stock")
    public ResponseEntity<Producto> actualizarStock(
            @PathVariable Integer id,
            @RequestParam Integer cantidad) {
        try {
            return ResponseEntity.ok(inventarioService.actualizarStock(id, cantidad));
        } catch (IllegalArgumentException e) {
            log.warn("Error al actualizar stock: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error al actualizar stock", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/producto/scan/{id}")
    public ResponseEntity<Map<String, Object>> obtenerProductoPorId(
            @PathVariable Integer id) {
        try {
            ProductoDTO producto = inventarioService.obtenerProductoPorId(id);
            if (producto == null) {
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> response = getStringObjectMap(producto);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al buscar producto por ID: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    private static @NotNull Map<String, Object> getStringObjectMap(ProductoDTO producto) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", producto.getIdProducto());
        response.put("nombre", producto.getNombre());
        response.put("descripcion", producto.getDescripcion());
        response.put("precioVenta", producto.getPrecioVenta());
        response.put("stockActual", producto.getStockActual());
        response.put("stockMinimo", producto.getStockMinimo());
        response.put("categoria", producto.getCategoriaNombre());
        response.put("proveedor", producto.getProveedorNombre());
        response.put("activo", producto.getActivo());
        return response;
    }
}
