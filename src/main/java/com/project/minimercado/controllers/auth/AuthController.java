package com.project.minimercado.controllers.auth;

import com.project.minimercado.model.login.LoginRequest;
import com.project.minimercado.model.login.LoginResponse;
import com.project.minimercado.model.login.Token;
import com.project.minimercado.model.register.RegisterRequest;
import com.project.minimercado.model.register.RegisterResponse;
import com.project.minimercado.services.auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private Token token2;

    public AuthController(AuthService authService) {
        this.authService = authService;

    }

    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = authService.login(loginRequest);
            if ("success".equals(response.getStatus())) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + response.getMessage())
                        .header("X-Content-Type-Options", "nosniff")
                        .header("X-Frame-Options", "DENY")
                        .header("X-XSS-Protection", "1; mode=block")
                        .body(response);
            }
            return ResponseEntity.badRequest()
                    .header("X-Content-Type-Options", "nosniff")
                    .header("X-Frame-Options", "DENY")
                    .header("X-XSS-Protection", "1; mode=block")
                    .body(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new LoginResponse("error", "Error interno del servidor"));
        }
    }

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            RegisterResponse response = authService.register(registerRequest);

            if ("success".equals(response.getStatus())) {
                return ResponseEntity.ok()
                        .header("X-Content-Type-Options", "nosniff")
                        .header("X-Frame-Options", "DENY")
                        .header("X-XSS-Protection", "1; mode=block")
                        .body(response);

            }

            return ResponseEntity.badRequest()
                    .header("X-Content-Type-Options", "nosniff")
                    .header("X-Frame-Options", "DENY")
                    .header("X-XSS-Protection", "1; mode=block")
                    .body(response);
        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .body(new RegisterResponse("error", "Error interno del servidor"));

        }
    }

    @PostMapping(value = "/logout", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> logout(@Valid @RequestBody String token) {
        try {

            String n= authService.logout(token);


            if (n.equals("success")) {
                return ResponseEntity.ok()
                        .header("X-Content-Type-Options", "nosniff")
                        .header("X-Frame-Options", "DENY")
                        .header("X-XSS-Protection", "1; mode=block")
                        .body("Logout success");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Internal server error");

        }
return ResponseEntity.internalServerError().body("No se reconocio el error");    }
}
