package com.project.minimercado;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MinimercadoApplication {


    public static void main(String[] args) {

        SpringApplication.run(MinimercadoApplication.class, args);
    }

}
