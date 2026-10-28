package com.example.mpaz_pro_6springboot.common.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

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

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml"
                        ).permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").hasRole("UTP")
                        .requestMatchers("/api/auth/me").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/auth/logout").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/*/cursos").authenticated()
                        .requestMatchers("/api/usuarios", "/api/usuarios/**").hasRole("UTP")
                        .requestMatchers(HttpMethod.POST, "/api/docencias", "/api/docencias/**").hasRole("UTP")
                        .requestMatchers(HttpMethod.PUT, "/api/docencias", "/api/docencias/**").hasRole("UTP")
                        .requestMatchers(HttpMethod.DELETE, "/api/docencias", "/api/docencias/**").hasRole("UTP")
                        .requestMatchers("/api/docencias", "/api/docencias/**").authenticated()
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/contenidos", "/api/contenidos/**",
                                "/api/actividades", "/api/actividades/**",
                                "/api/preguntas", "/api/preguntas/**",
                                "/api/alternativas", "/api/alternativas/**"
                        ).hasAnyRole("UTP", "DOCENTE")
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/unidades/*/envio-revision"
                        ).hasRole("DOCENTE")
                        .requestMatchers(HttpMethod.POST, CATALOGO).hasRole("UTP")
                        .requestMatchers(HttpMethod.PUT, CATALOGO).hasRole("UTP")
                        .requestMatchers(HttpMethod.DELETE, CATALOGO).hasRole("UTP")
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/asignaciones",
                                "/api/asignaciones/**"
                        ).hasRole("DOCENTE")
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/asignaciones",
                                "/api/asignaciones/**"
                        ).hasRole("DOCENTE")
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/asignaciones",
                                "/api/asignaciones/**"
                        ).hasRole("DOCENTE")
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/estudiantes/*/actividades/*/intentos"
                        ).hasRole("ESTUDIANTE")
                        .requestMatchers(HttpMethod.PATCH, "/api/intentos/*/completar")
                        .hasRole("ESTUDIANTE")
                        .requestMatchers(HttpMethod.POST, "/api/intentos/*/respuestas")
                        .hasRole("ESTUDIANTE")
                        .requestMatchers(HttpMethod.POST, "/api/informes-pedagogicos")
                        .hasRole("DOCENTE")
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/informes-pedagogicos/*/validacion"
                        ).hasRole("DOCENTE")
                        .requestMatchers(
                                "/api/informes-pedagogicos",
                                "/api/informes-pedagogicos/**"
                        ).hasRole("DOCENTE")
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
