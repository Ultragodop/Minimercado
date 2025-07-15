package com.project.minimercado.services.auth.JWT;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisTokenService {

    private final String TOKEN_PREFIX = "valid_token:";

    private final StringRedisTemplate redisTemplate;

    public RedisTokenService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveToken(String token, Duration duration) {
        redisTemplate.opsForValue().set(TOKEN_PREFIX + token, "valid", duration);
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
