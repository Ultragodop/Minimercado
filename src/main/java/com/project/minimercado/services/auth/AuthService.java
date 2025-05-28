package com.project.minimercado.services.auth;


import com.project.minimercado.model.bussines.Usuario;
import com.project.minimercado.model.login.LoginRequest;
import com.project.minimercado.model.login.LoginResponse;


import com.project.minimercado.model.register.RegisterRequest;
import com.project.minimercado.model.register.RegisterResponse;
import com.project.minimercado.repository.bussines.UsuarioRepository;

import com.project.minimercado.services.auth.JWT.JWTService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final BCryptPasswordEncoder encryptor = new BCryptPasswordEncoder(5);
    /*
     * Inyeccion de dependencias resumido con autowired
     * */

    JWTService jwtService;

    AuthenticationManager authManager;


     UsuarioRepository userRepository;

    public AuthService( JWTService jwtService,
                       AuthenticationManager authManager,
                        UsuarioRepository userRepository) {
        this.jwtService = jwtService;
        this.authManager = authManager;
        this.userRepository = userRepository;
    }
    public LoginResponse login(LoginRequest loginRequest) {
        System.out.println("Intentando iniciar sesion: {}" + loginRequest.getUsername());

        if (loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
            return new LoginResponse("error", "Parametros invalidos");
        }

        Usuario user = userRepository.findByNombre(loginRequest.getUsername());

        if (user != null) {

            if (encryptor.matches(loginRequest.getPassword(), user.getPasswordHash())) {

                return new LoginResponse("success", "Login exitoso");
            } else {

                return new LoginResponse("error", "Contraseña incorrecta");
            }
        }


        return new LoginResponse("error", "UsuarioRepository no encontrado");
    }

    public RegisterResponse register(RegisterRequest registerRequest) {
        System.out.println("Attempting registration for user: {}" + registerRequest.getUsername());

        if (registerRequest.getUsername() == null || registerRequest.getPassword() == null || registerRequest.getRol() == null) {
            return new RegisterResponse("error", "Invalid request parameters");
        }

        Usuario existingUser = userRepository.findByNombre(registerRequest.getUsername());
        if (existingUser != null) {
            System.out.println("Username already exists: {}" + registerRequest.getUsername());
            return new RegisterResponse("error", "El usuario ya existe");
        }

        try {
            Usuario newUser = new Usuario();
            newUser.setNombre(registerRequest.getUsername());
            newUser.setRol(registerRequest.getRol());
            newUser.setPasswordHash(encryptor.encode(registerRequest.getPassword()));
            userRepository.save(newUser);
            return new RegisterResponse("success", "Registro exitoso");
        } catch (Exception e) {
            System.out.println("Error registering user: {}" + registerRequest.getUsername() + e);
            return new RegisterResponse("error", "Error registering user: " + e.getMessage());
        }
    }


    public String verify(Usuario usuario) {
        Authentication authentication =
                authManager.authenticate(new UsernamePasswordAuthenticationToken(usuario.getNombre(), usuario.getPasswordHash()));
        if (authentication.isAuthenticated()) {
            System.out.println("Parte de la autenticacion con exito: " + authentication.isAuthenticated());

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            System.out.println(userDetails.getUsername() + "Parte de la autenticacion con exito: " + userDetails.isAccountNonExpired());
            String role = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse("inventario");
            System.out.println("Usuario autenticado: " + usuario.getNombre() + " con rol: " + role);
            return jwtService.generateToken(usuario.getNombre(), role);
        }
            System.out.println("Usuario no autenticado");
        return "error";


    }
}
