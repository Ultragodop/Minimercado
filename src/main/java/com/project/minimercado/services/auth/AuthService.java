package com.project.minimercado.services.auth;


import com.project.minimercado.model.login.LoginRequest;
import com.project.minimercado.model.login.LoginResponse;
import com.project.minimercado.model.login.User;
import com.project.minimercado.model.register.RegisterRequest;
import com.project.minimercado.model.register.RegisterResponse;
import com.project.minimercado.repository.login.UserRepository;
import com.project.minimercado.repository.login.JWT.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    JWTService jwtService;
    @Autowired
    AuthenticationManager authManager;

    @Autowired
    UserRepository userRepository;

    public LoginResponse login(LoginRequest loginRequest) {
        System.out.println("Intentando iniciar sesion: {}" + loginRequest.getUsername());

        if (loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
            return new LoginResponse("error", "Parametros invalidos");
        }

        User user = userRepository.findByUsername(loginRequest.getUsername());

        if (user != null) {

            if (encryptor.matches(loginRequest.getPassword(), user.getPassword())) {
                System.out.println("Login successful for user: {}" + user.getUsername());
                return new LoginResponse("success", "Login exitoso");
            } else {
                System.out.println("Invalid password for user: {}" + user.getUsername());
                return new LoginResponse("error", "Contraseña incorrecta");
            }
        }

        System.out.println("User not found: {} " + loginRequest.getUsername());
        return new LoginResponse("error", "UsuarioRepository no encontrado");
    }

    public RegisterResponse register(RegisterRequest registerRequest) {
        System.out.println("Attempting registration for user: {}" + registerRequest.getUsername());

        if (registerRequest.getUsername() == null || registerRequest.getPassword() == null) {
            return new RegisterResponse("error", "Invalid request parameters");
        }

        User existingUser = userRepository.findByUsername(registerRequest.getUsername());
        if (existingUser != null) {
            System.out.println("Username already exists: {}" + registerRequest.getUsername());
            return new RegisterResponse("error", "El usuario ya existe");
        }

        try {
            User newUser = new User();
            newUser.setUsername(registerRequest.getUsername());
            newUser.setPassword(encryptor.encode(registerRequest.getPassword()));
            userRepository.save(newUser);
            System.out.println("Successfully registered user: {}" + newUser.getUsername());
            return new RegisterResponse("success", "Registro exitoso");
        } catch (Exception e) {
            System.out.println("Error registering user: {}" + registerRequest.getUsername() + e);
            return new RegisterResponse("error", "Error registering user: " + e.getMessage());
        }
    }


    public String verify(User user) {
        Authentication authentication =
                authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        if (authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String role = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse("ROLE_USER");

            return jwtService.generateToken(user.getUsername(), role);
        }

        return "error";


    }
}
