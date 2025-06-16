package com.project.minimercado.services.auth.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Service
public class JWTService {
    private final Map<String, String> tokeninhash = new ConcurrentHashMap<>();


    //Esto es una mala practica ya que hardcodea la jwt secret key
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiration:86400000}") // 24 horas por defecto
    private long jwtExpiration;

    private SecretKey key;

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

        String token = createToken(claims, username); // Solo una vez
        System.out.println(token);

        tokeninhash.put(token, username);



        return token; // Retornás el mismo que guardaste
    }



    public boolean validateToken(String token, UserDetails userDetails) {

        if (!tokeninhash.containsKey(token)) {
            throw new IllegalArgumentException("Token not found in the hash map");

        }
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }
        if (extractExpiration(token).before(new Date())) {
            return false;
        }
        try {
            final String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && isValidTokenFormat(token);
        } catch (Exception e) {
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
        token = token.trim().replace("\"", "");

        System.out.println("Invalidating token: [" + token + "]");
        System.out.println("Hash: " + token.hashCode());

        if (!tokeninhash.containsKey(token)) {
            System.out.println("❌ Token no encontrado en el mapa");
            return "error";
        }

        tokeninhash.remove(token);
        System.out.println("Token invalidado correctamente");
        return "success";
    }

    public void printTokenKeys() {
        System.out.println("Claves en tokeninhash:");
        for (String key : tokeninhash.keySet()) {
            System.out.println("  🔑 " + key);
            System.out.println("    Hash: " + key.hashCode());
        }
    }

}
