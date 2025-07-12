package com.project.minimercado.repository.chat;

import com.project.minimercado.model.chat.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @Modifying
    @Query(value= "insert into ChatMessage ( sala, usuario, mensaje,timestamp) values (?1, ?2, ?3, ?4)")
    void insert(String sala,String usuario, String mensaje, Timestamp timestamp);

}
