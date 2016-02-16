package org.ado.biblio.error;

/**
 * @author Andoni del Olmo
 * @since 16.02.16
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}