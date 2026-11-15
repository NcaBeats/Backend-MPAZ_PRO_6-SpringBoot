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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Seguridad del backend: JWT stateless + reglas por rol.
 *
 * <p>Las lecturas de catalogo quedan disponibles para cualquier usuario
 * autenticado; las escrituras se limitan por rol. Las reglas de ownership
 * (por ejemplo, que un estudiante solo vea sus intentos) se validan en los
 * servicios, no aqui.</p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
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

                        // El listado de usuarios es administrativo.
                        .requestMatchers(HttpMethod.GET,
                                "/api/usuarios", "/api/usuarios/**"
                        ).hasRole("UTP")

                        // El consolidado preliminar es una herramienta docente.
                        .requestMatchers(HttpMethod.GET,
                                "/api/informes-pedagogicos/preliminar"
                        ).hasAnyRole("UTP", "DOCENTE")

                        // Escrituras administrativas de identidad y catalogos.
                        .requestMatchers(HttpMethod.POST,
                                "/api/usuarios", "/api/usuarios/**",
                                "/api/roles", "/api/roles/**",
                                "/api/niveles", "/api/niveles/**",
                                "/api/secciones", "/api/secciones/**",
                                "/api/cursos", "/api/cursos/**",
                                "/api/asignaturas", "/api/asignaturas/**",
                                "/api/estados-autorizacion", "/api/estados-autorizacion/**",
                                "/api/tipos-actividad", "/api/tipos-actividad/**",
                                "/api/tipos-recurso", "/api/tipos-recurso/**",
                                "/api/asignaturas-curso", "/api/asignaturas-curso/**",
                                "/api/usuarios-curso", "/api/usuarios-curso/**"
                        ).hasRole("UTP")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/usuarios", "/api/usuarios/**",
                                "/api/roles", "/api/roles/**",
                                "/api/niveles", "/api/niveles/**",
                                "/api/secciones", "/api/secciones/**",
                                "/api/cursos", "/api/cursos/**",
                                "/api/asignaturas", "/api/asignaturas/**",
                                "/api/estados-autorizacion", "/api/estados-autorizacion/**",
                                "/api/tipos-actividad", "/api/tipos-actividad/**",
                                "/api/tipos-recurso", "/api/tipos-recurso/**",
                                "/api/asignaturas-curso", "/api/asignaturas-curso/**",
                                "/api/usuarios-curso", "/api/usuarios-curso/**"
                        ).hasRole("UTP")
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/usuarios", "/api/usuarios/**",
                                "/api/roles", "/api/roles/**",
                                "/api/niveles", "/api/niveles/**",
                                "/api/secciones", "/api/secciones/**",
                                "/api/cursos", "/api/cursos/**",
                                "/api/asignaturas", "/api/asignaturas/**",
                                "/api/estados-autorizacion", "/api/estados-autorizacion/**",
                                "/api/tipos-actividad", "/api/tipos-actividad/**",
                                "/api/tipos-recurso", "/api/tipos-recurso/**",
                                "/api/asignaturas-curso", "/api/asignaturas-curso/**",
                                "/api/usuarios-curso", "/api/usuarios-curso/**"
                        ).hasRole("UTP")

                        // Escrituras de material pedagogico.
                        .requestMatchers(HttpMethod.POST,
                                "/api/unidades", "/api/unidades/**",
                                "/api/objetivos-aprendizaje", "/api/objetivos-aprendizaje/**",
                                "/api/objetivos-aprendizaje-unidad", "/api/objetivos-aprendizaje-unidad/**",
                                "/api/objetivos-aprendizaje-contenido", "/api/objetivos-aprendizaje-contenido/**",
                                "/api/contenidos", "/api/contenidos/**",
                                "/api/recursos", "/api/recursos/**",
                                "/api/actividades", "/api/actividades/**",
                                "/api/actividades-curso", "/api/actividades-curso/**",
                                "/api/preguntas", "/api/preguntas/**",
                                "/api/alternativas", "/api/alternativas/**",
                                "/api/alternativas-pregunta", "/api/alternativas-pregunta/**",
                                "/api/preguntas-actividad", "/api/preguntas-actividad/**"
                        ).hasAnyRole("UTP", "DOCENTE")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/unidades", "/api/unidades/**",
                                "/api/objetivos-aprendizaje", "/api/objetivos-aprendizaje/**",
                                "/api/objetivos-aprendizaje-unidad", "/api/objetivos-aprendizaje-unidad/**",
                                "/api/objetivos-aprendizaje-contenido", "/api/objetivos-aprendizaje-contenido/**",
                                "/api/contenidos", "/api/contenidos/**",
                                "/api/recursos", "/api/recursos/**",
                                "/api/actividades", "/api/actividades/**",
                                "/api/actividades-curso", "/api/actividades-curso/**",
                                "/api/preguntas", "/api/preguntas/**",
                                "/api/alternativas", "/api/alternativas/**",
                                "/api/alternativas-pregunta", "/api/alternativas-pregunta/**",
                                "/api/preguntas-actividad", "/api/preguntas-actividad/**"
                        ).hasAnyRole("UTP", "DOCENTE")
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/unidades", "/api/unidades/**",
                                "/api/objetivos-aprendizaje", "/api/objetivos-aprendizaje/**",
                                "/api/objetivos-aprendizaje-unidad", "/api/objetivos-aprendizaje-unidad/**",
                                "/api/objetivos-aprendizaje-contenido", "/api/objetivos-aprendizaje-contenido/**",
                                "/api/contenidos", "/api/contenidos/**",
                                "/api/recursos", "/api/recursos/**",
                                "/api/actividades", "/api/actividades/**",
                                "/api/actividades-curso", "/api/actividades-curso/**",
                                "/api/preguntas", "/api/preguntas/**",
                                "/api/alternativas", "/api/alternativas/**",
                                "/api/alternativas-pregunta", "/api/alternativas-pregunta/**",
                                "/api/preguntas-actividad", "/api/preguntas-actividad/**"
                        ).hasAnyRole("UTP", "DOCENTE")

                        // Escrituras de informes pedagogicos.
                        .requestMatchers(HttpMethod.POST,
                                "/api/informes-pedagogicos", "/api/informes-pedagogicos/**",
                                "/api/detalles-informe-pedagogico", "/api/detalles-informe-pedagogico/**"
                        ).hasAnyRole("UTP", "DOCENTE")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/informes-pedagogicos", "/api/informes-pedagogicos/**",
                                "/api/detalles-informe-pedagogico", "/api/detalles-informe-pedagogico/**"
                        ).hasAnyRole("UTP", "DOCENTE")
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/informes-pedagogicos", "/api/informes-pedagogicos/**",
                                "/api/detalles-informe-pedagogico", "/api/detalles-informe-pedagogico/**"
                        ).hasAnyRole("UTP", "DOCENTE")

                        // Flujo de evaluacion del estudiante.
                        .requestMatchers(HttpMethod.POST,
                                "/api/intentos", "/api/intentos/**",
                                "/api/respuestas", "/api/respuestas/**"
                        ).hasRole("ESTUDIANTE")

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
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
