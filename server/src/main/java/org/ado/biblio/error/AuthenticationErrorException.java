package org.ado.biblio.error;

public class AuthenticationErrorException extends RuntimeException {

    public AuthenticationErrorException() {
        super("Invalid username or password");
    }

}