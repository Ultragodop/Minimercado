package com.project.minimercado.services.Redis;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisService {
    private static final Logger logger = LoggerFactory.getLogger(RedisService.class);

    private final String TOKEN_PREFIX = "valid_token:";

    private final StringRedisTemplate redisTemplate;

    public RedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveToken(String token, Duration duration) {
        try {
        redisTemplate.opsForValue().set(TOKEN_PREFIX + token, "valid", duration);
        } catch (Exception e) {
            logger.error(e.getMessage());

        }
    }

    public boolean isTokenValid(String token) {
        return Boolean.TRUE.equals(
                redisTemplate.hasKey(TOKEN_PREFIX + token)
        );
    }
public MessageListenerAdapter getMessageListener(String message) {

}
public RedisMessageListenerContainer getRedisMessageListenerContainer() {

}
    public void revokeToken(String token) {
        redisTemplate.delete(TOKEN_PREFIX + token);
    }
}
