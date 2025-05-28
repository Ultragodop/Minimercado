package com.project.minimercado.controllers.auth;

import com.project.minimercado.model.bussines.Usuario;
import com.project.minimercado.model.register.RegisterRequest;
import com.project.minimercado.model.register.RegisterResponse;
import com.project.minimercado.services.auth.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/login")
    public String procesarLogin(
            @ModelAttribute Usuario user,
            HttpServletResponse response,
            RedirectAttributes flash
    ) {
        String token = authService.verify(user);

        if ("error".equals(token)) {
            // credenciales inválidas → volvemos al login con flash message
            flash.addFlashAttribute("error", "Usuario o contraseña incorrectos");
            return "redirect:/login";
        }

        // token válido → lo enviamos en la cabecera y redirigimos al dashboard
        response.setHeader("Authorization", "Bearer " + token);
        return "redirect:/dashboard";
    }
    @PostMapping("/register")
    public RegisterResponse register(@RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }


}
