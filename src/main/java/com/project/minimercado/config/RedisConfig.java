package com.project.minimercado.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    /**
     * Configura el bean 'RedisTemplate'.
     * Este es el cliente principal para interactuar con Redis.
     * Se configura con serializadores para manejar claves (Strings) y valores (objetos JSON).
     *
     * @param connectionFactory La factoría de conexión a Redis, inyectada automáticamente por Spring Boot.
     * @return Una instancia configurada de RedisTemplate.
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory); //


        template.setKeySerializer(new StringRedisSerializer());

        template.setValueSerializer(new StringRedisSerializer());


        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }


}