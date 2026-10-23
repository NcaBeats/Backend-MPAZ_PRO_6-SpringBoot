package com.example.mpaz_pro_6springboot.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                // Basic Auth se valida en cada solicitud: no se guarda estado en sesion.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Actuator
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        // Swagger/OpenAPI
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/v3/api-docs.yaml").permitAll()
                        // Autenticacion: el filtro Basic valida las credenciales del login.
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        // El registro lo realiza unicamente la UTP (administrador del sistema).
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").hasRole("UTP")
                        .requestMatchers("/api/auth/me").authenticated()
                        // Gestion de usuarios reservada a UTP.
                        .requestMatchers("/api/usuarios/**").hasRole("UTP")
                        // Informes: requieren autenticacion. El Service aplica
                        // la regla por dueno (un estudiante solo ve los suyos).
                        .requestMatchers("/api/informes-pedagogicos/**").authenticated()
                        // El resto de rutas sigue publico mientras se migran las reglas por rol.
                        .anyRequest().permitAll()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
