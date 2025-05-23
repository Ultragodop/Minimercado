package com.project.minimercado.repository.login.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {
    private String secretkey = "";

    public JWTService() {
        try {
            KeyGenerator keygen = KeyGenerator.getInstance("HmacSHA256"); // el algoritmo que se va a usar para generar la key
            SecretKey key = keygen.generateKey(); // te genera una key con el algoritmo especificado
            secretkey = Base64.getEncoder().encodeToString(key.getEncoded()); // la key la codificas en base64 y la pasas a string, la metes despues en tu secretkey
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Te extrae el username del token
     *
     * @param token
     * @return
     */

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Te extrae un valor especifico de los claims( claims son los datos que se guardan en el token "fragmentos de informacion codificados dentro del token")
     *
     * @param token
     * @return
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Te extrae todos los claims del token
     *
     * @param token
     * @return
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Te dice si el token es valido o no mediante el username y si no expiro
     *
     * @param token
     * @param userDetails
     * @return
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Te dice si el token ya expiro o no
     *
     * @param token
     * @return
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Te extrae la fecha de expiracion del token
     *
     * @param token
     * @return
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    /**
     * Te genera un token a partir de un username que es valido por 10 horas, especificado en .expiration
     *
     * @param username
     * @return
     */
    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 60 * 60 * 30))
                .and()
                .signWith(getKey())
                .compact();// 10 hours

    }

    /**
     * Te genera una key a partir de un secretkey que despues vas a usar para firmar el token
     * toma pa bo lo entendi a la perfeccion
     *
     * @return keybytes
     * @see <a href=secretkey></a>secretkey
     */
    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretkey);

        return Keys.hmacShaKeyFor(keyBytes);
    }
}
