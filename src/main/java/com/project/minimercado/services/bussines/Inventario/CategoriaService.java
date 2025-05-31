package com.project.minimercado.services.bussines.Inventario;

import com.project.minimercado.model.bussines.Categoria;
import com.project.minimercado.repository.bussines.CategoriasRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService {
    private final CategoriasRepository categoriasRepository;

    public CategoriaService(CategoriasRepository categoriasRepository) {
        this.categoriasRepository = categoriasRepository;
    }

    @Transactional
    public Categoria crearCategoria(Categoria categoria) {
        validarCategoria(categoria);
        return categoriasRepository.save(categoria);
    }

    @Transactional
    public Categoria actualizarCategoria(Integer id, Categoria categoriaActualizada) {
        Categoria categoriaExistente = obtenerCategoriaPorId(id);
        
        // Actualizar campos
        categoriaExistente.setNombre(categoriaActualizada.getNombre());
        
        validarCategoria(categoriaExistente);
        return categoriasRepository.save(categoriaExistente);
    }

    @Transactional
    public void eliminarCategoria(Integer id) {
        Categoria categoria = obtenerCategoriaPorId(id);
        
        // Verificar si hay productos asociados
        if (!categoria.getProductos().isEmpty()) {
            throw new RuntimeException("No se puede eliminar la categoría porque tiene productos asociados");
        }
        
        categoriasRepository.delete(categoria);
    }

    @Transactional(readOnly = true)
    public Categoria obtenerCategoriaPorId(Integer id) {
        return categoriasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Categoria> listarCategorias() {
        return categoriasRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Categoria buscarCategoriaPorNombre(String nombre) {
        return categoriasRepository.findAll().stream()
                .filter(c -> c.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con nombre: " + nombre));
    }

    @Transactional(readOnly = true)
    public boolean existeCategoria(String nombre) {
        return categoriasRepository.findAll().stream()
                .anyMatch(c -> c.getNombre().equalsIgnoreCase(nombre));
    }

    private void validarCategoria(Categoria categoria) {
        if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre de la categoría es requerido");
        }
        
        // Validar longitud del nombre
        if (categoria.getNombre().length() > 100) {
            throw new RuntimeException("El nombre de la categoría no puede tener más de 100 caracteres");
        }
        
        // Validar que no exista otra categoría con el mismo nombre
        if (existeCategoria(categoria.getNombre()) && categoria.getId() == null) {
            throw new RuntimeException("Ya existe una categoría con el nombre: " + categoria.getNombre());
        }
    }
} 