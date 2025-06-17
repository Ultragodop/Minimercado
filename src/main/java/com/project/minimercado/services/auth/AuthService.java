package com.project.minimercado.services.auth;


import com.project.minimercado.model.bussines.Usuario;
import com.project.minimercado.model.login.LoginRequest;
import com.project.minimercado.model.login.LoginResponse;

import com.project.minimercado.model.register.RegisterRequest;
import com.project.minimercado.model.register.RegisterResponse;
import com.project.minimercado.repository.bussines.UsuarioRepository;
import com.project.minimercado.services.auth.JWT.JWTService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class AuthService {
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9_]{3,20}$";
    private final BCryptPasswordEncoder encryptor = new BCryptPasswordEncoder(12);
    private final JWTService jwtService;


    private final AuthenticationManager authManager;


    private final UsuarioRepository usuarioRepository;

    public AuthService(JWTService jwtService, AuthenticationManager authManager, UsuarioRepository usuarioRepository) {
        this.jwtService = jwtService;
        this.authManager = authManager;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional

    public LoginResponse login(LoginRequest loginRequest) {
        try {
            if (!isValidLoginRequest(loginRequest)) {
                return new LoginResponse("error", "Parámetros inválidos");
            }

            Usuario usuario = usuarioRepository.findByNombre(loginRequest.getUsername());


            if (usuario == null) {
                return new LoginResponse("error", "Usuario no encontrado");
            }


            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            if (authentication.isAuthenticated()) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                System.out.println(userDetails.getAuthorities());
                String role = userDetails.getAuthorities().stream()
                        .findFirst()
                        .map(GrantedAuthority::getAuthority)
                        .orElse("ROLE_USER");

                String token = jwtService.generateToken(userDetails.getUsername(), role);


                return new LoginResponse("success", token);
            }

            return new LoginResponse("error","401", "Autenticación fallida");
        } catch (BadCredentialsException e) {
            return new LoginResponse("error","401", "Credenciales inválidas");
        } catch (Exception e) {
            return new LoginResponse("error","500", "Error en el servidor: " + e.getMessage());
        }
    }

    @Transactional
    public RegisterResponse register(RegisterRequest registerRequest) {
        try {
            if (!isValidRegisterRequest(registerRequest)) {
                return new RegisterResponse("error", "Parámetros inválidos");
            }

            Usuario existingUsuario = usuarioRepository.findByNombre(registerRequest.getUsername());
            if (existingUsuario != null) {
                return new RegisterResponse("error", "El usuario ya existe");
            }

            Usuario newUsuario = new Usuario();
            newUsuario.setNombre(registerRequest.getUsername());
            newUsuario.setPasswordHash(encryptor.encode(registerRequest.getPassword()));
            newUsuario.setRol(registerRequest.getRol() != null ? registerRequest.getRol() : "USER");

            usuarioRepository.save(newUsuario);
            return new RegisterResponse("success", "Registro exitoso");
        } catch (Exception e) {
            return new RegisterResponse("error", "Error en el registro: " + e.getMessage());
        }
    }

    public String logout(String token) {
        System.out.println(token);
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token no puede ser nulo o vacío");
        }
        String m = jwtService.InvalidateToken(token);
        if (m.equals("success")) {
            return "success";
        }
        return "error";
    }

    private boolean isValidLoginRequest(LoginRequest request) {
        return request != null &&
                StringUtils.hasText(request.getUsername()) &&
                StringUtils.hasText(request.getPassword()) &&
                request.getPassword().length() >= MIN_PASSWORD_LENGTH;
    }

    private boolean isValidRegisterRequest(RegisterRequest request) {
        return request != null &&
                StringUtils.hasText(request.getUsername()) &&
                request.getUsername().matches(USERNAME_PATTERN) &&
                StringUtils.hasText(request.getPassword()) &&
                request.getPassword().length() >= MIN_PASSWORD_LENGTH;
    }


}
