package com.example.mpaz_pro_6springboot.common;

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
}
