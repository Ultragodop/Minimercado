package com.project.minimercado.repository.chat;

import com.project.minimercado.model.chat.SalaChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalaChatRepository extends JpaRepository<SalaChat, Long> {


    Optional<SalaChat> findByNombre(String salaNombre);
}