package com.project.minimercado.model.register;


import com.project.minimercado.model.peticiones.Response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class RegisterResponse extends Response {
    public RegisterResponse(String message, String status) {
        super(message, status);
    }
}
