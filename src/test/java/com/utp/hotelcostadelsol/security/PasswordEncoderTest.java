package com.utp.hotelcostadelsol.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
