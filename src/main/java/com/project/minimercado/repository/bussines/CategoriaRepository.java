package com.project.minimercado.repository.bussines;

import com.project.minimercado.model.bussines.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    boolean existsByNombre(String nombre);
    Optional<Categoria> findByNombre(String nombre);
} 