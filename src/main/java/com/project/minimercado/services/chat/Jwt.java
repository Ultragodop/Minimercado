package com.project.minimercado.services.chat;

import com.project.minimercado.services.auth.JWT.JWTService;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

        ServletServerHttpResponse servletResp = (ServletServerHttpResponse) response;
        HttpServletResponse resp = servletResp.getServletResponse();;
        URI uri = request.getURI();
        String query = uri.getQuery();
        logger.info("JWT query: " + query);
        String token = null;

        if (query != null) {
            for (String param : query.split("&")) {
                if (param.startsWith("token=")) {
                    token = param.substring("token=".length());
                    logger.info("Token obtenido de la solicitud WebSocket: {}", token);
                    break;
                }
            }
        }


        if (token == null || token.isEmpty()) {
            logger.warn("Token no proporcionado en la solicitud WebSocket");


            resp.sendError((HttpStatus.UNAUTHORIZED.value()), "Token no proporcionado");
            return false;
        }

        if (!jwtService.isValidTokenFormat(token) || !jwtService.isTokenStored(token)) {
            logger.warn("Token no valido en la solicitud WebSocket");
            resp.sendError(HttpStatus.UNAUTHORIZED.value(), "Token no válido o no almacenado");

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
