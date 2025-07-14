package com.project.minimercado.repository.chat;

import com.project.minimercado.model.bussines.Usuario;
import com.project.minimercado.model.chat.SalaChat;
import com.project.minimercado.model.chat.SalaUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface salaUsuarioRepository extends JpaRepository<SalaUsuario, Integer> {
    boolean existsBySalaAndUsuario(SalaChat sala, Usuario usuario);

    Usuario findAllBySala(SalaChat sala);
}
