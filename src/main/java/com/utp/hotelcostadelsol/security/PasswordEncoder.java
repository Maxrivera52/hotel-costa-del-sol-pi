package com.utp.hotelcostadelsol.security;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

/**
 * Componente de seguridad criptografica para hashing y verificacion de contraseñas
 * mediante el algoritmo BCrypt (factor de costo 12), aplicando Google Guava Preconditions
 * y Apache Commons Lang3 para tratamiento seguro de credenciales.
 */
public final class PasswordEncoder {

    private static final int COST_FACTOR = 12;

    private PasswordEncoder() {
        // Clase utilitaria inmutable
    }

    /**
     * Genera un hash BCrypt seguro a partir de una contraseña en texto plano.
     *
     * @param rawPassword contraseña en texto plano
     * @return hash BCrypt con salt integrado
     */
    public static String hashPassword(String rawPassword) {
        Preconditions.checkArgument(StringUtils.isNotBlank(rawPassword),
                "La contraseña no puede ser nula ni vacia");
        return BCrypt.withDefaults().hashToString(COST_FACTOR, rawPassword.toCharArray());
    }

    /**
     * Verifica si una contraseña en texto plano coincide con un hash BCrypt almacenado.
     *
     * @param rawPassword    contraseña ingresada
     * @param hashedPassword hash BCrypt almacenado
     * @return true si el hash es valido y coincide, false en caso contrario
     */
    public static boolean verifyPassword(String rawPassword, String hashedPassword) {
        if (StringUtils.isAnyBlank(rawPassword, hashedPassword)) {
            return false;
        }
        BCrypt.Result result = BCrypt.verifyer().verify(rawPassword.toCharArray(), hashedPassword.trim());
        return result.verified;
    }
}
