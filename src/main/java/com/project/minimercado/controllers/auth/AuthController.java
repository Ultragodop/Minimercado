package com.project.minimercado.controllers.auth;

import com.project.minimercado.model.bussines.Usuario;
import com.project.minimercado.model.login.LoginRequest;
import com.project.minimercado.model.login.LoginResponse;
import com.project.minimercado.model.register.RegisterRequest;
import com.project.minimercado.model.register.RegisterResponse;
import com.project.minimercado.repository.bussines.UsuarioRepository;
import com.project.minimercado.services.auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UsuarioRepository usuarioRepository;

    public AuthController(AuthService authService, UsuarioRepository usuarioRepository) {
        this.authService = authService;
        this.usuarioRepository = usuarioRepository;

    }


    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {


        try {
            LoginResponse response = authService.login(loginRequest);


            if ("success".equals(response.getStatus())) {

                return ResponseEntity.ok()
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + response.getToken())
                        .header("X-Content-Type-Options", "nosniff")
                        .header("X-Frame-Options", "DENY")
                        .header("X-XSS-Protection", "1; mode=block")
                        .body(response);
            }


            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header("X-Content-Type-Options", "nosniff")
                    .header("X-Frame-Options", "DENY")
                    .header("X-XSS-Protection", "1; mode=block")
                    .body(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new LoginResponse("error", "500", "Error interno del servidor"));
        }
    }

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            RegisterResponse response = authService.register(registerRequest);

            if ("success".equals(response.getStatus())) {
                return ResponseEntity.status(200)
                        .header("X-Content-Type-Options", "nosniff")
                        .header("X-Frame-Options", "DENY")
                        .header("X-XSS-Protection", "1; mode=block")
                        .body(response);

            }

            return ResponseEntity.status(401)
                    .header("X-Content-Type-Options", "nosniff")
                    .header("X-Frame-Options", "DENY")
                    .header("X-XSS-Protection", "1; mode=block")
                    .body(response);
        } catch (Exception e) {

            return ResponseEntity.status(500)
                    .body(new RegisterResponse("error", "Error interno del servidor"));

        }
    }

    @PostMapping(value = "/logout", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> logout(@Valid @RequestBody String token) {
        try {

            String response = authService.logout(token);


            if (response.equals("success")) {
                return ResponseEntity.ok()
                        .header("X-Content-Type-Options", "nosniff")
                        .header("X-Frame-Options", "DENY")
                        .header("X-XSS-Protection", "1; mode=block")
                        .body("Logout success");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .header("X-Content-Type-Options", "nosniff")
                    .header("X-Frame-Options", "DENY")
                    .header("X-XSS-Protection", "1; mode=block")
                    .body("Logout fallido: Token inválido o no encontrado");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("X-Content-Type-Options", "nosniff")
                    .header("X-Frame-Options", "DENY")
                    .header("X-XSS-Protection", "1; mode=block")
                    .body("Error interno del servidor");

        }
    }


        }



