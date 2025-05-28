package com.project.minimercado.repository.bussines;

import com.project.minimercado.model.bussines.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
Usuario findByNombre(String username);
}
