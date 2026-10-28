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

    /**
     * Catalogo curricular: lo consulta cualquier usuario autenticado, pero solo
     * la UTP lo crea, edita o elimina (incluida la autorizacion de unidades).
     */
    private static final String[] CATALOGO = {
            "/api/asignaturas", "/api/asignaturas/**",
            "/api/cursos", "/api/cursos/**",
            "/api/unidades", "/api/unidades/**",
            "/api/objetivos-aprendizaje", "/api/objetivos-aprendizaje/**",
            "/api/contenidos", "/api/contenidos/**",
            "/api/recursos-contenido", "/api/recursos-contenido/**",
            "/api/actividades", "/api/actividades/**",
            "/api/preguntas", "/api/preguntas/**",
            "/api/alternativas", "/api/alternativas/**"
    };

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
                        // Errores: el dispatch a /error no debe quedar bloqueado.
                        .requestMatchers("/error").permitAll()
                        // Autenticacion: el filtro Basic valida las credenciales del login.
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        // El registro y la gestion de usuarios los realiza unicamente la UTP.
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").hasRole("UTP")
                        .requestMatchers("/api/auth/me").authenticated()
                        .requestMatchers("/api/usuarios", "/api/usuarios/**").hasRole("UTP")
                        // Catalogo: escritura reservada a UTP.
                        .requestMatchers(HttpMethod.POST, CATALOGO).hasRole("UTP")
                        .requestMatchers(HttpMethod.PUT, CATALOGO).hasRole("UTP")
                        .requestMatchers(HttpMethod.DELETE, CATALOGO).hasRole("UTP")
                        // Docencia: el docente (o la UTP) gestiona asignaciones, OA y actividades.
                        .requestMatchers(HttpMethod.POST, "/api/asignaciones", "/api/asignaciones/**").hasAnyRole("DOCENTE", "UTP")
                        .requestMatchers(HttpMethod.PUT, "/api/asignaciones", "/api/asignaciones/**").hasAnyRole("DOCENTE", "UTP")
                        .requestMatchers(HttpMethod.DELETE, "/api/asignaciones", "/api/asignaciones/**").hasAnyRole("DOCENTE", "UTP")
                        // Evaluacion: el estudiante es el unico que inicia, completa y responde.
                        .requestMatchers(HttpMethod.POST, "/api/estudiantes/*/actividades/*/intentos").hasRole("ESTUDIANTE")
                        .requestMatchers(HttpMethod.PATCH, "/api/intentos/*/completar").hasRole("ESTUDIANTE")
                        .requestMatchers(HttpMethod.POST, "/api/intentos/*/respuestas").hasRole("ESTUDIANTE")
                        // Informes: lectura autenticada (el Service limita al dueno);
                        // generacion y validacion reservadas a DOCENTE/UTP.
                        .requestMatchers(HttpMethod.POST, "/api/informes-pedagogicos").hasAnyRole("DOCENTE", "UTP")
                        .requestMatchers(HttpMethod.PATCH, "/api/informes-pedagogicos/*/validacion").hasAnyRole("DOCENTE", "UTP")
                        .requestMatchers("/api/informes-pedagogicos", "/api/informes-pedagogicos/**").authenticated()
                        // El resto de rutas requiere autenticacion (nunca publico por defecto).
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
