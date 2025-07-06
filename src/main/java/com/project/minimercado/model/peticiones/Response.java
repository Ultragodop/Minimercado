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
    private Long id;
    private String httpError;

    public Response(String status, String token, Long id) {
        this.status = status;
        this.token = token;
        this.id = id;
    }

    public Response(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public Response(String status, String numero, String message) {
        this.status = status;
        this.httpError = numero;
        this.message = message;


    }

}
