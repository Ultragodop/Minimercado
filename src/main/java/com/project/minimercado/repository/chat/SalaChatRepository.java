package com.project.minimercado.repository.chat;

import com.project.minimercado.model.chat.SalaChat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SalaChatRepository extends JpaRepository<SalaChat, Long> {

    Optional<SalaChat> findByNombre(String salaNombre);
}