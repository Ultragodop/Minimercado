package com.project.minimercado.repository.bussines;

import com.project.minimercado.dto.UsuarioDTO;
import com.project.minimercado.model.bussines.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    @Query("SELECT u FROM Usuario u WHERE (u.nombre) = (:nombre)")
    Optional<Usuario> findByNombreas(String nombre);

    @Query("SELECT u.id AS id FROM Usuario u WHERE u.nombre = :nombre")
    UsuarioDTO findId(@Param("nombre") String nombre);

    @Query("SELECT u.id FROM Usuario u WHERE LOWER(u.nombre) = LOWER(:nombre)")
    Long getIdUsuario(@Param("nombre") String nombre);

    Usuario getUsuarioById(Long idUsuario);

    Usuario findByNombre(String nombre);
}
