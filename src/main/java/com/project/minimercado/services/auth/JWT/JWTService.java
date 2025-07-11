package com.project.minimercado.services.auth.JWT;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.project.minimercado.utils.UserDetailsWithId;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Service
public class JWTService {

    Cache<String, Boolean> tokeninhash = Caffeine.newBuilder()
            .expireAfterWrite(15, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();


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
        System.out.println(claims);
        String token = createToken(claims, username);
        System.out.println(token);

        tokeninhash.put(token, true);
        return token;
    }


    public boolean validateToken(String token, UserDetailsWithId userDetails) {
        Boolean isValid = tokeninhash.getIfPresent(token);
        if (Boolean.FALSE.equals(isValid) || isValid == null) {
            throw new IllegalArgumentException("No se encontró el token en el cache o el token es inválido");

        }
        System.out.println(" Token encontrado en el mapa");
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }
        if (extractExpiration(token).before(new Date())) {
            return false;
        }
        try {
            final String username = extractUsername(token);
            System.out.println("Username extraído del token: " + username);
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
        if(token.startsWith("\"") && token.endsWith("\"")) {
            System.out.println("Token con comillas detectado, eliminando comillas...");
            token = token.trim().replace("\"", "");
        }


        System.out.println("Invalidando token: [" + token + "]");
        System.out.println("Hash: " + token.hashCode());

        if (tokeninhash.getIfPresent(token) == null) {
            System.out.println("Token no encontrado en el mapa");
            return "error";
        }


        tokeninhash.put(token, false);
        System.out.println("Token invalidado correctamente");
        return "success";
    }

    public boolean isTokenStored(String token) {

        if (token == null || token.isEmpty()) {
            System.out.println("Token is null or empty");
            return false;
        }

        return tokeninhash.get(token, k -> false);
    }


}
