package com.project.minimercado.config;
import com.project.minimercado.exception.ChatWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final ChatWebSocketHandler chatWebSocketHandler;
    public WebSocketConfig(){
    this.chatWebSocketHandler = new ChatWebSocketHandler();
    }// en vez de crar una nueva instancia de chatwebsockethandler en cada peticion, se inyecta chatwebsockethandler en websocketconfig para despues usarla en cada peticion
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new ChatWebSocketHandler(), "/chat").setAllowedOrigins("*");
    }
}




