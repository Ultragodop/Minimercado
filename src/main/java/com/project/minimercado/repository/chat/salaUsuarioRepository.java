package com.project.minimercado.repository.chat;

import com.project.minimercado.model.bussines.Usuario;
import com.project.minimercado.model.chat.SalaChat;
import com.project.minimercado.model.chat.SalaUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface salaUsuarioRepository extends JpaRepository<SalaUsuario, Integer> {
    boolean existsBySalaAndUsuario(SalaChat sala, Usuario usuario);
    @Query(value = "select u.nombre from usuarios u join sala_usuarios su on u.id_usuario=su.id_usuario join salachat sch on su.sala_id=sch.id where sch.nombre= :salaNombre and u.nombre!= :nombreUsuario" , nativeQuery = true)
    String getSalaNombre(String salaNombre, String nombreUsuario);

}
