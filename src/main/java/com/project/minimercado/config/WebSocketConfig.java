package com.project.minimercado.config;

import com.project.minimercado.websocket.ChatWebSocketHandler;
import com.project.minimercado.services.chat.Jwt;
import com.project.minimercado.websocket.GameSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatWebSocketHandler chatWebSocketHandler;
    private final Jwt jwtHandshakeInterceptor;


    public WebSocketConfig( ChatWebSocketHandler chatWebSocketHandler, Jwt jwtHandshakeInterceptor) {

        this.chatWebSocketHandler = chatWebSocketHandler;
        this.jwtHandshakeInterceptor = jwtHandshakeInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        registry.addHandler(chatWebSocketHandler, "/chat/**")
                .addInterceptors(jwtHandshakeInterceptor)
                .setAllowedOrigins("*");
    }
}


