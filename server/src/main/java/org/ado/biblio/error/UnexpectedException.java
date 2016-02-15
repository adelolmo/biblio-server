package org.ado.biblio.error;

public class UnexpectedException extends RuntimeException {

    private final String action;

    public UnexpectedException(String action, String message) {
        super(message);
        this.action = action;
    }

    public String action() {
        return action;
    }

    public String message() {
        return getMessage();
    }

}
