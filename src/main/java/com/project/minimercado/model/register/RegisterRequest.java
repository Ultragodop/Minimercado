package com.project.minimercado.model.register;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.minimercado.model.peticiones.Request;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class RegisterRequest extends Request {
    private String username;
    private String password;

    @JsonCreator
    public RegisterRequest(@JsonProperty("username") String username,
                           @JsonProperty("password") String password) {
        this.username = username;
        this.password = password;
    }

    public RegisterRequest() {
    }

}
