package com.project.minimercado.repository.login;


import com.project.minimercado.model.bussines.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<Usuario, Integer> {

}


