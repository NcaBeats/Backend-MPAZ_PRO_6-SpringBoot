package com.example.mpaz_pro_6springboot.common;

import com.example.mpaz_pro_6springboot.common.security.CustomUserDetails;
import com.example.mpaz_pro_6springboot.common.security.JwtService;
import com.example.mpaz_pro_6springboot.identidad.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * Base para los tests de integracion: levanta un PostgreSQL real con
 * Testcontainers y lo conecta al contexto de Spring mediante
 * {@link ServiceConnection}. Requiere un daemon de Docker accesible.
 *
 * <p>El contenedor es un singleton estatico para que todos los contextos de
 * Spring (que Spring Test cachea entre clases) compartan la misma base. Se
 * inicia una sola vez y lo detiene Ryuk al terminar la JVM.</p>
 */
public abstract class PostgresIntegrationTest {

    @ServiceConnection
    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17-alpine");

    static {
        POSTGRES.start();
    }

    @Autowired
    protected JwtService jwtService;

    @Autowired
    protected UsuarioRepository usuarioRepository;

    /**
     * Genera un bearer token para un usuario del seed y lo agrega a la
     * solicitud. Evita que cada test tenga que hacer login manualmente.
     */
    protected RequestPostProcessor bearer(String username) {
        return request -> {
            var usuario = usuarioRepository.findByUsername(username).orElseThrow();
            String token = jwtService.generateToken(new CustomUserDetails(usuario));
            request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            return request;
        };
    }
}
