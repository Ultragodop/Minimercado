package com.project.minimercado.services.bussines.Inventario;

import com.project.minimercado.model.bussines.Categoria;
import com.project.minimercado.model.bussines.Producto;
import com.project.minimercado.model.bussines.Proveedores;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InventarioService {
    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final ProveedorService proveedorService;

    public InventarioService(ProductoService productoService,
                           CategoriaService categoriaService,
                           ProveedorService proveedorService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.proveedorService = proveedorService;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> obtenerEstadoInventario() {
        Map<String, Object> estado = new HashMap<>();
        
        // Obtener productos con stock bajo
        List<Producto> productosBajoStock = productoService.obtenerProductosBajoStock();
        
        // Obtener todos los productos activos
        List<Producto> productosActivos = productoService.listarProductosActivos();
        
        // Calcular valor total del inventario
        double valorTotalInventario = productosActivos.stream()
                .mapToDouble(p -> p.getPrecioCompra() * p.getStockActual())
                .sum();

        estado.put("productosBajoStock", productosBajoStock);
        estado.put("totalProductosActivos", productosActivos.size());
        estado.put("valorTotalInventario", valorTotalInventario);
        
        return estado;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> obtenerResumenPorCategoria() {
        Map<String, Object> resumen = new HashMap<>();
        
        List<Categoria> categorias = categoriaService.listarCategorias();
        
        for (Categoria categoria : categorias) {
            List<Producto> productosCategoria = productoService.buscarProductosPorCategoria(categoria);
            
            Map<String, Object> infoCategoria = new HashMap<>();
            infoCategoria.put("totalProductos", productosCategoria.size());
            
            double valorInventarioCategoria = productosCategoria.stream()
                    .mapToDouble(p -> p.getPrecioCompra() * p.getStockActual())
                    .sum();
            
            infoCategoria.put("valorInventario", valorInventarioCategoria);
            
            // Productos bajo stock en esta categoría
            long productosBajoStock = productosCategoria.stream()
                    .filter(p -> p.getStockActual() <= p.getStockMinimo())
                    .count();
            
            infoCategoria.put("productosBajoStock", productosBajoStock);
            
            resumen.put(categoria.getNombre(), infoCategoria);
        }
        
        return resumen;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> obtenerResumenPorProveedor() {
        Map<String, Object> resumen = new HashMap<>();
        
        List<Proveedores> proveedores = proveedorService.listarProveedores();
        
        for (Proveedores proveedor : proveedores) {
            Map<String, Object> infoProveedor = new HashMap<>();
            
            // Obtener productos del proveedor
            List<Producto> productosProveedor = proveedor.getProductos().stream().toList();
            
            infoProveedor.put("totalProductos", productosProveedor.size());
            
            double valorInventarioProveedor = productosProveedor.stream()
                    .mapToDouble(p -> p.getPrecioCompra() * p.getStockActual())
                    .sum();
            
            infoProveedor.put("valorInventario", valorInventarioProveedor);
            
            // Productos bajo stock de este proveedor
            long productosBajoStock = productosProveedor.stream()
                    .filter(p -> p.getStockActual() <= p.getStockMinimo())
                    .count();
            
            infoProveedor.put("productosBajoStock", productosBajoStock);
            
            resumen.put(proveedor.getNombre(), infoProveedor);
        }
        
        return resumen;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> obtenerAlertasInventario() {
        return productoService.obtenerProductosBajoStock().stream()
                .map(producto -> {
                    Map<String, Object> alerta = new HashMap<>();
                    alerta.put("producto", producto.getNombre());
                    alerta.put("stockActual", producto.getStockActual());
                    alerta.put("stockMinimo", producto.getStockMinimo());
                    alerta.put("categoria", producto.getIdCategoria().getNombre());
                    alerta.put("proveedor", producto.getIdProveedor().getNombre());
                    return alerta;
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> obtenerEstadisticasInventario() {
        List<Producto> productos = productoService.listarProductosActivos();
        
        Map<String, Object> estadisticas = new HashMap<>();
        
        // Total de productos diferentes
        estadisticas.put("totalProductos", productos.size());
        
        // Total de unidades en inventario
        int totalUnidades = productos.stream()
                .mapToInt(Producto::getStockActual)
                .sum();
        estadisticas.put("totalUnidades", totalUnidades);
        
        // Valor total del inventario
        double valorTotal = productos.stream()
                .mapToDouble(p -> p.getPrecioCompra() * p.getStockActual())
                .sum();
        estadisticas.put("valorTotal", valorTotal);
        
        // Productos bajo stock
        long productosBajoStock = productos.stream()
                .filter(p -> p.getStockActual() <= p.getStockMinimo())
                .count();
        estadisticas.put("productosBajoStock", productosBajoStock);
        
        // Total de categorías
        long totalCategorias = categoriaService.listarCategorias().size();
        estadisticas.put("totalCategorias", totalCategorias);
        
        // Total de proveedores
        long totalProveedores = proveedorService.listarProveedores().size();
        estadisticas.put("totalProveedores", totalProveedores);
        
        return estadisticas;
    }
} 