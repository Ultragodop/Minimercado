package com.project.minimercado.services.chat;

import com.project.minimercado.services.auth.JWT.JWTService;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

@Component
public class Jwt implements HandshakeInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(Jwt.class);
    private final JWTService jwtService;


    public Jwt(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, @NotNull ServerHttpResponse response,
                                   @NotNull WebSocketHandler wsHandler, @NotNull Map<String, Object> attributes) throws IOException {
        String token = null;
        ServletServerHttpResponse servletResp = (ServletServerHttpResponse) response;
        HttpServletResponse resp = servletResp.getServletResponse();
        HttpHeaders requestToken= request.getHeaders();
        for (String headerName : requestToken.keySet()) {
            if( headerName.equalsIgnoreCase("Authorization")) {
                String authHeader = requestToken.getFirst(headerName);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                    logger.info("Token obtenido de la cabecera Authorization: {}", token);
                } else {
                    logger.warn("Cabecera Authorization no contiene un token válido");
                }
            }
        }
        //request.getHeaders().forEach((key, value) -> logger.info("Header: {} = {}", key, value));


        //Como estaba antes, se obtiene el token de la URI
        //Pero esto es más inseguro, ya que el bearer token puede ser expuesto en la URL
        /**
         * if (query != null) {
            for (String param : query.split("&")) {
                if (param.startsWith("token=")) {
                    token = param.substring("token=".length());
                    logger.info("Token obtenido de la solicitud WebSocket: {}", token);
                    break;
                }
            }
        }
*/

        if (token == null || token.isEmpty()) {
            logger.warn("Token no proporcionado en la solicitud WebSocket");


            resp.sendError((HttpStatus.UNAUTHORIZED.value()), "Token no proporcionado");
            return false;
        }

        if (!jwtService.isValidTokenFormat(token) || !jwtService.isTokenStored(token)) {
            logger.warn("Token no valido en la solicitud WebSocket");
            resp.sendError(HttpStatus.UNAUTHORIZED.value(), "Token no válido");

            return false;
        }
        logger.info("Token válido en la solicitud WebSocket: {}", token);


        attributes.put("token", token);
        logger.info("JWT validado en la solicitud WebSocket");
        attributes.put("username", jwtService.extractUsername(token));


        return true;
    }

    /**
     * Método que se ejecuta después de completar el handshake.
     * No se utiliza en este caso, pero es necesario implementarlo
     * Para cumplir con la interfaz HandshakeInterceptor.
     *
     * @param request
     * @param response
     * @param wsHandler
     * @param exception
     */
    @Override
    public void afterHandshake(
            @NotNull ServerHttpRequest request,
            @NotNull ServerHttpResponse response,
            @NotNull WebSocketHandler wsHandler,
            Exception exception) {
        logger.info("Interceptando handler: {}", wsHandler.getClass().getName());
    }
}
