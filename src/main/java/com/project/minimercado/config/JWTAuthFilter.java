package com.project.minimercado.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.project.minimercado.services.auth.JWT.JWTService;
import com.project.minimercado.utils.UserDetailsServiceWithId;
import com.project.minimercado.utils.UserDetailsWithId;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.*;

@Component
@RequiredArgsConstructor
public class JWTAuthFilter extends OncePerRequestFilter {
    private final JWTService jwtService;
    private final UserDetailsServiceWithId userDetailsService;
    private Cache<String, Boolean> validTokenCache;
    private Cache<String, UserDetailsWithId> userDetailsCache;
    private ExecutorService authExecutor;

    @PostConstruct
    public void init() {

        validTokenCache = Caffeine.newBuilder()
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .maximumSize(1000)
                .build();

        userDetailsCache = Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .maximumSize(500)
                .build();

        authExecutor = Executors.newWorkStealingPool(8);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/") ||
                path.startsWith("/api/public/") ||
                path.startsWith("/public/") ||
                path.startsWith("/chat/") ||
                path.startsWith("/ws/") ||
                path.contains("swagger") ||
                path.contains("api-docs");


    }

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            final String authHeader = request.getHeader("Authorization");

            // Paso 1: Verificación rápida de header
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                sendError(response, "El header tiene que venir con el token malparido", HttpServletResponse.SC_UNAUTHORIZED);
                filterChain.doFilter(request, response);
                return;
            }

            final String jwt = authHeader.substring(7);

            if(jwt.isBlank()) {
                sendError(response, "El header no puede estar vacio", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            // 2) Si el token sigue almacenado en JWTService, resuelvo UserDetails y seteo Authentication
            if (!jwtService.isTokenStored(jwt)) {
                sendError(response, "Token invalidado o no existe", HttpServletResponse.SC_FORBIDDEN);
                return;
            }



                if(jwtService.isTokenStored(jwt)){
                // Extraigo username del JWT (sin lanzar más validaciones, pues ya está cacheado en JWTService)
                String username = jwtService.extractUsername(jwt);
                System.out.println("Username extraído del JWT: " + username);
                if (username != null) {
                    // Busco UserDetails en caché o en el servicio
                    UserDetailsWithId userDetails = userDetailsCache.getIfPresent(username);
                    if (userDetails == null) {
                        userDetails = userDetailsService.loadUserByUsername(username);
                        userDetailsCache.put(username, userDetails);
                    }

                    setAuthentication(userDetails, request);
                }
                filterChain.doFilter(request, response);
                return;
            }



            // Paso 3: Validación de estructura básica
            if (!jwtService.isValidTokenFormat(jwt)) {
                sendError(response, "Invalid token format", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // Paso 4: Extracción async de user details
            final String username = jwtService.extractUsername(jwt);
            if (username == null) {
                sendError(response, "Username not found in token", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            long start = System.currentTimeMillis();
            // Paso 5: Carga async con caché y timeout
            UserDetailsWithId userDetails = loadUserDetailsAsync(username).get(2000, TimeUnit.MILLISECONDS);

            long duration = System.currentTimeMillis() - start;
            System.out.println("Tiempo carga UserDetails para " + username + ": " + duration + " ms");

            // Paso 6: Validación final del token
            if (jwtService.validateToken(jwt, userDetails)) {
                validTokenCache.put(jwt, true);  // Almacenar en caché
                setAuthentication(userDetails, request);
                filterChain.doFilter(request, response);
            } else {
                sendError(response, "Token validation failed", HttpServletResponse.SC_UNAUTHORIZED);
            }

        } catch (TimeoutException e) {
            sendError(response, "Authentication timeout", HttpServletResponse.SC_GATEWAY_TIMEOUT);
        } catch (ExecutionException e) {
            sendError(response, "User details service error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (JwtException | AuthenticationException e) {
            sendError(response, "Authentication error: " + e.getMessage(), HttpServletResponse.SC_UNAUTHORIZED);
        } catch (Exception e) {
            sendError(response, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private CompletableFuture<UserDetailsWithId> loadUserDetailsAsync(String username) {
        return CompletableFuture.supplyAsync(() -> {
            try {

                UserDetailsWithId cached = userDetailsCache.getIfPresent(username);
                if (cached != null) return cached;

                UserDetailsWithId details = userDetailsService.loadUserByUsername(username);
                userDetailsCache.put(username, details);
                return details;
            } catch (UsernameNotFoundException e) {
                throw new CompletionException(e);
            }
        }, authExecutor);
    }

    private void setAuthentication(UserDetailsWithId userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authToken);
        SecurityContextHolder.setContext(context);
    }

    private void sendError(HttpServletResponse response, String message, int status) throws IOException {
        SecurityContextHolder.clearContext();
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
        response.getWriter().flush();
    }

    @PreDestroy
    public void shutdown() {
        authExecutor.shutdown();
        try {
            if (!authExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                authExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            authExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
