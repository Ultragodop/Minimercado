package com.project.minimercado.model.login;


import com.project.minimercado.model.peticiones.Response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor  // <-- necesario para puto jackson de re mierda jackson puto
public class LoginResponse extends Response {
    public LoginResponse(String status, String token) {
        super(status, token);
    }
    public LoginResponse(String status, String numero, String response) {
        super(status, numero, response);
    }
}





