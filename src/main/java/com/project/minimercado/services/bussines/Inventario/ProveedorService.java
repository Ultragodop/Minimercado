package com.project.minimercado.services.bussines.Inventario;

import com.project.minimercado.dto.bussines.Inventario.ProductoDTO;
import com.project.minimercado.dto.bussines.Inventario.ProveedorDTO;
import com.project.minimercado.dto.bussines.Inventario.ProveedorProductosDTO;
import com.project.minimercado.model.bussines.Producto;
import com.project.minimercado.model.bussines.Proveedores;
import com.project.minimercado.repository.bussines.ProveedoresRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class ProveedorService {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
    );
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{8,15}$");
    private final ProveedoresRepository proveedoresRepository;


    public ProveedorService(ProveedoresRepository proveedoresRepository) {
        this.proveedoresRepository = proveedoresRepository;
    }

    @Transactional
    public String crearProveedor(Proveedores proveedor) {
        validarProveedor(proveedor);
        return "Proveedor creado exitosamente con ID: " + proveedoresRepository.save(proveedor).getId();
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
    public List<ProveedorDTO> listarProveedoresDTO() {
        List<ProveedorDTO> proveedoresDTOS = proveedoresRepository.findAllProveedoresDTOs();
        if (proveedoresDTOS.isEmpty()) {
            throw new RuntimeException("No se encontraron proveedores");
        }
        for (ProveedorDTO proveedorDTO : proveedoresDTOS){
            System.out.println(proveedorDTO.getNombre());
        }

        return proveedoresDTOS;
    }
    @Transactional(readOnly = true)
    public List<Proveedores> listarProveedores() {
        return proveedoresRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Proveedores buscarProveedorPorNombre(String nombre) {
        return proveedoresRepository.findByNombre(nombre).stream()
                .filter(p -> p.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con nombre: " + nombre));
    }

    @Transactional(readOnly = true)
    public boolean existeProveedor(String nombre ) {
        return proveedoresRepository.findByNombre(nombre).stream()
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
