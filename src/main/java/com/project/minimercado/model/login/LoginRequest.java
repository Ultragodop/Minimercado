package com.project.minimercado.model.login;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.minimercado.model.peticiones.Request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest extends Request {
    @NotBlank(message = "El nombre de usuario es requerido")
    @Pattern(regexp = "^[a-zA-Z0-9_]{3,20}$", message = "El nombre de usuario debe tener entre 3 y 20 caracteres alfanuméricos")
    private String username;

    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    @JsonCreator
    public LoginRequest(
            @JsonProperty("username") String username,
            @JsonProperty("password") String password
    ) {
        this.username = username;
        this.password = password;
    }

    public LoginRequest() {
    }
}


