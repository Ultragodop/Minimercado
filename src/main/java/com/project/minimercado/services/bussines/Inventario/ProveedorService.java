package com.project.minimercado.services.bussines.Inventario;

import com.project.minimercado.model.bussines.Proveedores;
import com.project.minimercado.repository.bussines.ProveedoresRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProveedorService {
    private final ProveedoresRepository proveedoresRepository;

    public ProveedorService(ProveedoresRepository proveedoresRepository) {
        this.proveedoresRepository = proveedoresRepository;
    }

    @Transactional
    public Proveedores crearProveedor(Proveedores proveedor) {
        validarProveedor(proveedor);
        return proveedoresRepository.save(proveedor);
    }

    @Transactional
    public void actualizarProveedor(Proveedores proveedor) {
        if (proveedor.getId() == null) {
            throw new RuntimeException("El ID del proveedor es requerido para actualizar");
        }

        if (!proveedoresRepository.existsById(proveedor.getId())) {
            throw new RuntimeException("Proveedor no encontrado");
        }

        validarProveedor(proveedor);
        proveedoresRepository.save(proveedor);
    }

    @Transactional(readOnly = true)
    public List<Proveedores> listarProveedores() {
        return proveedoresRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Proveedores> buscarProveedorPorId(Integer id) {
        return proveedoresRepository.findById(id);
    }

    @Transactional
    public void eliminarProveedor(Integer id) {
        Proveedores proveedor = proveedoresRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        // Verificar si tiene productos asociados
        if (!proveedor.getProductos().isEmpty()) {
            throw new RuntimeException("No se puede eliminar el proveedor porque tiene productos asociados");
        }

        // Verificar si tiene pedidos asociados
        if (!proveedor.getPedidosProveedors().isEmpty()) {
            throw new RuntimeException("No se puede eliminar el proveedor porque tiene pedidos asociados");
        }

        proveedoresRepository.delete(proveedor);
    }

    private void validarProveedor(Proveedores proveedor) {
        if (proveedor == null) {
            throw new RuntimeException("El proveedor no puede ser nulo");
        }
        if (proveedor.getNombre() == null || proveedor.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre del proveedor es requerido");
        }
        // El teléfono y email son opcionales, pero si se proporcionan deben ser válidos
        if (proveedor.getTelefono() != null && proveedor.getTelefono().trim().isEmpty()) {
            throw new RuntimeException("El teléfono no puede estar vacío");
        }
        if (proveedor.getEmail() != null && !proveedor.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new RuntimeException("El email no tiene un formato válido");
        }
    }
} 