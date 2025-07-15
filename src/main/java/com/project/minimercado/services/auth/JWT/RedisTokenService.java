package com.project.minimercado.services.auth.JWT;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisTokenService {
    private static final Logger logger = LoggerFactory.getLogger(RedisTokenService.class);

    private final String TOKEN_PREFIX = "valid_token:";

    private final StringRedisTemplate redisTemplate;

    public RedisTokenService(StringRedisTemplate redisTemplate) {
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

    public void revokeToken(String token) {
        redisTemplate.delete(TOKEN_PREFIX + token);
    }
}
