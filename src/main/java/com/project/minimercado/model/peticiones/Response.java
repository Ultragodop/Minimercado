package com.project.minimercado.model.peticiones;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor  // <-- necesario para puto Jackson de re mierda
public class Response {
    private String status;
    private String message;

    public Response(String status, String message) {
        this.status = status;
        this.message = message;
    }
}
