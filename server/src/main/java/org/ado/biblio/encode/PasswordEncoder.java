package org.ado.biblio.encode;

import org.apache.commons.codec.digest.DigestUtils;

import java.security.SecureRandom;

/**
 * @author Andoni del Olmo
 * @since 16.02.16
 */
public class PasswordEncoder {

    public static PasswordHashed encode(final String password) {
        final byte[] saltBytes = new byte[32];
        new SecureRandom().nextBytes(saltBytes);
        final String salt = DigestUtils.sha256Hex(saltBytes);
        return new PasswordHashed(salt, DigestUtils.sha256Hex(salt + password));
    }
}