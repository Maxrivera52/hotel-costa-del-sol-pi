package com.utp.hotelcostadelsol.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias TDD para PasswordEncoder y Rol (Seguridad BCrypt + Google Guava + Apache Commons).
 */
class PasswordEncoderTest {

    @Test
    @DisplayName("Debe generar un hash BCrypt valido y verificar la contraseña correctamente")
    void shouldHashAndVerifyPassword() {
        String raw = "CostaDelSol2026!";
        String hash = PasswordEncoder.hashPassword(raw);

        assertNotNull(hash);
        assertTrue(hash.startsWith("$2a$12$"));
        assertTrue(PasswordEncoder.verifyPassword(raw, hash));
        assertFalse(PasswordEncoder.verifyPassword("ClaveIncorrecta", hash));
    }

    @Test
    @DisplayName("Debe verificar que los hashes iniciales en schema_postgresql.sql coincidan con admin123, recep123, client123 y piero123")
    void shouldVerifySeedCredentialsInSchemaSql() throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("schema/schema_postgresql.sql")) {
            assertNotNull(is, "No se encontro schema/schema_postgresql.sql en el classpath");
            String sql = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            assertTrue(sql.contains("Joel Cori Alarcon") && sql.contains("Piero"),
                    "El archivo schema_postgresql.sql debe contener a los integrantes actuales");
            assertTrue(PasswordEncoder.verifyPassword("admin123", extractHashForUser(sql, "admin")),
                    "El hash de 'admin' en schema_postgresql.sql no coincide con 'admin123'");
            assertTrue(PasswordEncoder.verifyPassword("recep123", extractHashForUser(sql, "recepcion")),
                    "El hash de 'recepcion' en schema_postgresql.sql no coincide con 'recep123'");
            assertTrue(PasswordEncoder.verifyPassword("client123", extractHashForUser(sql, "cliente")),
                    "El hash de 'cliente' en schema_postgresql.sql no coincide con 'client123'");
            assertTrue(PasswordEncoder.verifyPassword("piero123", extractHashForUser(sql, "piero")),
                    "El hash de 'piero' en schema_postgresql.sql no coincide con 'piero123'");
        }
    }

    private String extractHashForUser(String sql, String username) {
        Pattern p = Pattern.compile("'" + Pattern.quote(username) + "'\\s*,\\s*'([^']+)'");
        Matcher m = p.matcher(sql);
        assertTrue(m.find(), "No se encontro el usuario " + username + " en schema_postgresql.sql");
        return m.group(1);
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException con Google Guava Preconditions si la contraseña es vacia")
    void shouldRejectBlankPasswordOnHash() {
        assertThrows(IllegalArgumentException.class, () -> PasswordEncoder.hashPassword("   "));
        assertThrows(IllegalArgumentException.class, () -> PasswordEncoder.hashPassword(null));
    }

    @Test
    @DisplayName("Debe resolver roles por codigo con Apache Commons StringUtils")
    void shouldResolveRoleFromCode() {
        assertEquals(Rol.ROLE_ADMIN, Rol.fromCodigo("  role_admin "));
        assertEquals(Rol.ROLE_RECEPCION, Rol.fromCodigo("ROLE_RECEPCION"));
        assertNull(Rol.fromCodigo(""));
        assertNull(Rol.fromCodigo("ROLE_INEXISTENTE"));
    }
}
