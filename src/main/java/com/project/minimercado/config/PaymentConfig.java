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
    private String baseUrl;
    private String merchantSecret;
    private String commerceName;
    private String siteUrl;
    private String callbackUrl;
    private boolean testMode;


    @Bean
    public RestTemplate paymentRestTemplate() {
        return new RestTemplate();
    }
} 