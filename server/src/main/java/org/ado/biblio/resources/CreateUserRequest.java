package org.ado.biblio.resources;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Andoni del Olmo
 * @since 13.02.16
 */
public class CreateUserRequest {

    @JsonProperty
    private String username;

    @JsonProperty
    private String password;

    @JsonCreator
    public CreateUserRequest(@JsonProperty("username") final String username,
                             @JsonProperty("password") final String password) {
        this.username = username;
        this.password = password;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }
}