package com.example.mpaz_pro_6springboot.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

/**
 * Crea el directorio del archivo SQLite antes de que Flyway abra la conexion.
 *
 * sqlite-jdbc no crea carpetas: si el directorio de {@code SQLITE_PATH} no
 * existe, el arranque falla con SQLITE_CANTOPEN. Se registra en
 * META-INF/spring.factories para que corra antes de los beans y despues de
 * cargar application.yaml (y el .env importado).
 */
public class SqliteDirectoryInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Logger log = LoggerFactory.getLogger(SqliteDirectoryInitializer.class);
    private static final String PREFIX = "jdbc:sqlite:";

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        crearDirectorio(context.getEnvironment().getProperty("spring.datasource.url"));
    }

    static void crearDirectorio(String url) {
        if (url == null || !url.startsWith(PREFIX)) {
            return;
        }
        // Las bases en memoria no tocan el disco.
        if (url.contains(":memory:") || url.contains("mode=memory")) {
            return;
        }

        String archivo = url.substring(PREFIX.length()).split("\\?", 2)[0];
        if (archivo.startsWith("file:")) {
            archivo = archivo.substring("file:".length());
        }
        if (archivo.isBlank()) {
            return;
        }

        try {
            Path directorio = Path.of(archivo).toAbsolutePath().getParent();
            if (directorio != null) {
                Files.createDirectories(directorio);
                log.info("SQLite: directorio listo en {}", directorio);
            }
        } catch (InvalidPathException e) {
            log.warn("SQLite: no se pudo interpretar la ruta '{}'", archivo, e);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo crear el directorio de la base SQLite: " + archivo, e);
        }
    }
}
