package com.project.minimercado.config;

import com.project.minimercado.dto.chat.RedisWebSocketMessageReceiver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisPubSubConfig {
    @Bean
    public ChannelTopic websocketsMessagesTopic(){
        return new ChannelTopic("websockets-mensajes");
    }
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            MessageListenerAdapter listenerAdapter) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        container.addMessageListener(listenerAdapter, websocketsMessagesTopic());

        return container;
    }
    @Bean
    public MessageListenerAdapter messageListenerAdapter(RedisWebSocketMessageReceiver receiver) {

        return new MessageListenerAdapter(receiver, "receiveMessage");
    }
}

