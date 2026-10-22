package com.example.mpaz_pro_6springboot.common.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * sqlite-jdbc falla con SQLITE_CANTOPEN si el directorio del archivo no
 * existe. El inicializador debe crearlo antes de que Flyway conecte.
 */
class SqliteDirectoryInitializerTest {

    @TempDir
    Path tempDir;

    @Test
    void creaElDirectorioDelArchivoSqlite() {
        Path archivo = tempDir.resolve("datos/sub/mpaz.db");

        SqliteDirectoryInitializer.crearDirectorio("jdbc:sqlite:" + archivo + "?foreign_keys=on");

        assertTrue(Files.isDirectory(archivo.getParent()));
    }

    @Test
    void noCreaDirectoriosParaBasesEnMemoria() {
        Path archivo = tempDir.resolve("no-crear/memoria.db");

        assertDoesNotThrow(() -> SqliteDirectoryInitializer.crearDirectorio(
                "jdbc:sqlite:" + archivo + "?mode=memory&cache=shared"));
        assertDoesNotThrow(() -> SqliteDirectoryInitializer.crearDirectorio("jdbc:sqlite::memory:"));

        assertFalse(Files.exists(archivo.getParent()));
    }
}
