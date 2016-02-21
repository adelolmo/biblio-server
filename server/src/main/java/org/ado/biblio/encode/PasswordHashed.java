package org.ado.biblio.encode;

/**
 * @author Andoni del Olmo
 * @since 16.02.16
 */
public class PasswordHashed {

    private final String salt;
    private final String encodedPassword;

    public PasswordHashed(String salt, String encodedPassword) {
        this.salt = salt;
        this.encodedPassword = encodedPassword;
    }

    public String salt() {
        return salt;
    }

    public String encodedPassword() {
        return encodedPassword;
    }
}