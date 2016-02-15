package org.ado.biblio.error;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorMessage {

    private final int code;
    private final String message;

    public ErrorMessage(String message) {
        this(500, message);
    }

    @JsonCreator
    public ErrorMessage(@JsonProperty("code") int code, @JsonProperty("message") String message) {
        this.code = code;
        this.message = message;
    }

    @JsonProperty("code")
    public Integer getCode() {
        return code;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }
}