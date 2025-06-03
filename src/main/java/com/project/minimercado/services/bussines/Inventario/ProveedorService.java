package com.project.minimercado.services.bussines.Inventario;

import com.project.minimercado.dto.bussines.Inventario.ProveedorProductosDTO;
import com.project.minimercado.model.bussines.Proveedores;
import com.project.minimercado.repository.bussines.ProveedoresRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class ProveedorService {
    private final ProveedoresRepository proveedoresRepository;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[(]?[0-9]{3}[)]?[-\\s.]?[0-9]{3}[-\\s.]?[0-9]{4,6}$");

    public ProveedorService(ProveedoresRepository proveedoresRepository) {
        this.proveedoresRepository = proveedoresRepository;
    }

    @Transactional
    public Proveedores crearProveedor(Proveedores proveedor) {
        validarProveedor(proveedor);
        return proveedoresRepository.save(proveedor);
    }

    @Transactional
    public Proveedores actualizarProveedor(Integer id, Proveedores proveedorActualizado) {
        Proveedores proveedorExistente = (Proveedores) obtenerProveedorPorId(id);
        
        // Actualizar campos
        proveedorExistente.setNombre(proveedorActualizado.getNombre());
        proveedorExistente.setTelefono(proveedorActualizado.getTelefono());
        proveedorExistente.setEmail(proveedorActualizado.getEmail());
        proveedorExistente.setDireccion(proveedorActualizado.getDireccion());
        
        validarProveedor(proveedorExistente);
        return proveedoresRepository.save(proveedorExistente);
    }

    @Transactional
    public void eliminarProveedor(Integer id) {
        ProveedorProductosDTO proveedor = obtenerProveedorPorId(id);
        
        // Verificar si hay productos asociados
        if (!existeProveedor(proveedor.getNombre())) {
            throw new RuntimeException("No se puede eliminar el proveedor porque tiene productos asociados");
        }
        
        // Verificar si hay pedidos asociados
        if (!existeProveedor(proveedor.getPedidosProveedors())) {
            throw new RuntimeException("No se puede eliminar el proveedor porque tiene pedidos asociados");
        }
        
        proveedoresRepository.delete((Proveedores) proveedor);
    }

    @Transactional(readOnly = true)
    public ProveedorProductosDTO obtenerProveedorPorId(Integer id) {
        return proveedoresRepository.findProveedoresConProductos(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Proveedores> listarProveedores() {
        return proveedoresRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Proveedores buscarProveedorPorNombre(String nombre) {
        return proveedoresRepository.findAll().stream()
                .filter(p -> p.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con nombre: " + nombre));
    }

    @Transactional(readOnly = true)
    public boolean existeProveedor(String nombre) {
        return proveedoresRepository.findAll().stream()
                .anyMatch(p -> p.getNombre().equalsIgnoreCase(nombre));
    }

    @Transactional(readOnly = true)
    public List<Proveedores> buscarProveedoresPorEmail(String email) {
        return proveedoresRepository.findAll().stream()
                .filter(p -> p.getEmail() != null && p.getEmail().equalsIgnoreCase(email))
                .toList();
    }

    private void validarProveedor(Proveedores proveedor) {
        if (proveedor.getNombre() == null || proveedor.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre del proveedor es requerido");
        }
        
        // Validar longitud del nombre
        if (proveedor.getNombre().length() > 150) {
            throw new RuntimeException("El nombre del proveedor no puede tener más de 150 caracteres");
        }
        
        // Validar que no exista otro proveedor con el mismo nombre
        if (existeProveedor(proveedor.getNombre()) && proveedor.getId() == null) {
            throw new RuntimeException("Ya existe un proveedor con el nombre: " + proveedor.getNombre());
        }
        
        // Validar email si está presente
        if (proveedor.getEmail() != null && !proveedor.getEmail().isEmpty()) {
            if (proveedor.getEmail().length() > 150) {
                throw new RuntimeException("El email no puede tener más de 150 caracteres");
            }
            if (!EMAIL_PATTERN.matcher(proveedor.getEmail()).matches()) {
                throw new RuntimeException("El formato del email no es válido");
            }
        }
        
        // Validar teléfono si está presente
        if (proveedor.getTelefono() != null && !proveedor.getTelefono().isEmpty()) {
            if (proveedor.getTelefono().length() > 50) {
                throw new RuntimeException("El teléfono no puede tener más de 50 caracteres");
            }
            if (!PHONE_PATTERN.matcher(proveedor.getTelefono()).matches()) {
                throw new RuntimeException("El formato del teléfono no es válido");
            }
        }
    }
} 