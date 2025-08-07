package com.project.minimercado.services.auth.JWT;

import com.project.minimercado.services.Redis.RedisService;
import com.project.minimercado.utils.UserDetailsWithId;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Function;

@Slf4j
@Service
public class JWTService {

    private final RedisService redisService;

    //Esto es una mala practica ya que hardcodea la jwt secret key
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiration:86400000}") // 24 horas por defecto
    private long jwtExpiration;

    private SecretKey key;

    public JWTService(RedisService redisService) {
        this.redisService = redisService;
    }


    @PostConstruct
    public void init() {
        if (secretKey == null || secretKey.length() < 32) {
            throw new IllegalStateException("JWT secret key must be at least 32 characters long");
        }
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {

        final Claims claims = extractAllClaims(token);

        return claimsResolver.apply(claims);
    }


    public String generateToken(String username, String role) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        System.out.println(claims);
        String token = createToken(claims, username);
        System.out.println(token);
        try {
            redisService.saveToken(token, Duration.ofMinutes(30));
        } catch (Exception e) {
         System.out.println("Error al guardar el token en Redis: " + e.getMessage());
        }
        return token;
    }


    public boolean validateToken(String token, UserDetailsWithId userDetails) {
        if(redisService.isTokenValid(token)) {
            System.out.println("Token encontrado");
            if (token == null || token.isEmpty()) {
                throw new IllegalArgumentException("Token cannot be null or empty");
            }
            if (extractExpiration(token).before(new Date())) {
                return false;
            }
        }

            try {
                final String username = extractUsername(token);
                System.out.println("Username extraído del token: " + username);
                return username.equals(userDetails.getUsername()) && isValidTokenFormat(token);

            }

        catch (Exception e) {
            return false;
        }
    }

    public boolean isValidTokenFormat(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
    protected String createToken(Map<String, Object> claims, String subject) {
        System.out.println(claims + subject);
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .and()
                .signWith(key)
                .compact();

    }
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }
    public String InvalidateToken(String token) {
        if(token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }
        if(token.startsWith("\"") && token.endsWith("\"")) {
            System.out.println("Token con comillas detectado, eliminando comillas...");
            token = token.trim().replace("\"", "");
        }
        if(!isValidTokenFormat(token)) {
            System.out.println("Token no válido o no almacenado: " + token);
            return "error";
        }
        System.out.println("Invalidando token: [" + token + "]");
        redisService.revokeToken(token);
        return "success";
        }
    public boolean isTokenStored(String token) {
        if(token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }
        if(token.startsWith("\"") && token.endsWith("\"")) {
            System.out.println("Token con comillas detectado, eliminando comillas...");
            token = token.trim().replace("\"", "");
        }
        return redisService.isTokenValid(token);
    }




    }




