package com.project.minimercado.model.login;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Token {
    private String token;

    @JsonCreator
    public Token(@JsonProperty("token") String token) {
    }



}
