package com.project.minimercado.dto;

import com.project.minimercado.model.login.LoginResponse;

public class LoginResponseWithId {
    private LoginResponse response;
    private Long id;

    // constructor
    public LoginResponseWithId(LoginResponse response, Long id) {
        this.response = response;
        this.id = id;
    }

    // getters y setters
    public LoginResponse getResponse() {
        return response;
    }

    public void setResponse(LoginResponse response) {
        this.response = response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

