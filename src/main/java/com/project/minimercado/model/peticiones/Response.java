package com.project.minimercado.model.peticiones;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor  // <-- necesario para puto Jackson de re mierda
public class Response {
    private String status;
    private String token;
    private String message;
    private String httpError;
    public Response(String status, String token) {
        this.status = status;
        this.token = token;
    }
public Response(String status, String numero, String message) {
        this.status = status;
        this.httpError = numero;
        this.message = message;


}

}
