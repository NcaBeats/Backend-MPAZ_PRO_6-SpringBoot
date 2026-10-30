package com.example.mpaz_pro_6springboot.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuracion temporal de seguridad para la base del proyecto.
 *
 * <p>Spring Security esta en el classpath, pero aun no existe autenticacion
 * propia. Se dejan todas las rutas abiertas para poder usar Swagger y
 * /actuator/health mientras se construye el dominio. Al implementar login
 * (usuarios, roles y JWT), reemplazar este bloque por las reglas reales.</p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }
}
