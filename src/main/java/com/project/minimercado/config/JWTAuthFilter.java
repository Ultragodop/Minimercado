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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class JWTAuthFilter extends OncePerRequestFilter {
    private final JWTService jwtService;
    private final UserDetailsServiceWithId userDetailsService;
    private Cache<String, UserDetailsWithId> userDetailsCache;
    private ExecutorService authExecutor;

    @PostConstruct
    public void init() {
        userDetailsCache = Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .maximumSize(100)
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
    ) throws IOException {

        try {
            final String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                sendError(response, "El header tiene que venir con el token malparido", HttpServletResponse.SC_UNAUTHORIZED);
                filterChain.doFilter(request, response);
                return;
            }

            final String jwt = authHeader.substring(7);

            if (jwt.isBlank()) {
                sendError(response, "El header no puede estar vacio", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            if (!jwtService.isValidTokenFormat(jwt) || !jwtService.isTokenStored(jwt)) {
                sendError(response, "Token invalidado", HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            String username = jwtService.extractUsername(jwt);
            System.out.println(username);
            if (username != null) {

                UserDetailsWithId userDetails = userDetailsCache.getIfPresent(username);
                if (userDetails == null) {
                    userDetails = userDetailsService.loadUserByUsername(username);
                    userDetailsCache.put(username, userDetails);
                }

                if (!jwtService.validateToken(jwt, userDetails)) {
                    sendError(response, "Token expirado o invalidado", HttpServletResponse.SC_UNAUTHORIZED);
                }
                setAuthentication(userDetails, request);
            }
            filterChain.doFilter(request, response);
        } catch (JwtException | AuthenticationException e) {
            sendError(response, "Authentication error: " + e.getMessage(), HttpServletResponse.SC_UNAUTHORIZED);
        } catch (Exception e) {
            sendError(response, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
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
