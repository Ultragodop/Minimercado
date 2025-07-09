package com.project.minimercado.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConfigurationProperties(prefix = "payment")
@Data
public class PaymentConfig {
    private String baseUrl= "https://api.payments.arriba.uy/";
    private String merchantSecret="cb1668e9-cb22-41ad-99d2-1c0e6e5deb8c";
    private String commerceName="Fui yo no fuiste tu yo si fui";
    private String siteUrl= "apoalolala.com";
    private String callbackUrl= "F";// aca tendria que poner el endpoint de mi api que recibe el callback, es decir mi-ip-actual:3050/api/payments/callback
    private boolean testMode;

    @Bean
    public RestTemplate paymentRestTemplate() {
        return new RestTemplate();
    }
} 