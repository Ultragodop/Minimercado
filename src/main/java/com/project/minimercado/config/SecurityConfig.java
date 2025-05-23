package com.project.minimercado.config;


import com.project.minimercado.services.auth.MyUsrDtlsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//Problema que tenia con authenticationmanager era que habia una ciclo de dependencias,
// a que me refiero con esto, authservice dependia de authmanager y authmanager dependia de authservice,
// porque no sabia que se necesitaba crear una clase aparte para authenticationprovider,
// como authenticationprovider depende de authenticationmanager, se crea un ciclo de dependencias,
// por lo que se crea una clase aparte para authenticationprovider, y se inyecta en la clase securityconfig,
// y en la clase authservice se inyecta el authenticationprovider fua que capo que soy
@Configuration
public class SecurityConfig {
    private final JWTAuthFilter jwtAuthFilter;
    private final MyUsrDtlsService myUsrDtlsService;


    public SecurityConfig(JWTAuthFilter jwtAuthFilter, MyUsrDtlsService myUsrDtlsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.myUsrDtlsService = myUsrDtlsService;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/register", "/login", "/showlogin", "/showregister", "api/auth/verify").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/ventas/**").hasAnyRole("vendedor", "ADMIN")
                        .requestMatchers("//**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )

                .httpBasic(Customizer.withDefaults())
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)


                .build();

    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(new BCryptPasswordEncoder(5));

        authProvider.setUserDetailsService(myUsrDtlsService);
        System.out.println("myUsrDtlsService: " + myUsrDtlsService);
        return authProvider;

    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();

    }

}
