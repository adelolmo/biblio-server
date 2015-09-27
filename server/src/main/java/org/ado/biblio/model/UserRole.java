package org.ado.biblio.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserRole {

    ADMIN("ADMIN"),
    USER("USER");

    private String role;

    private UserRole(String role) {
        this.role = role;
    }

    @JsonValue
    public String getRole() {
        return role;
    }

    @Override
    public String toString() {
        return role;
    }

    @JsonCreator
    public static UserRole fromString(String role) {
        for (UserRole ur : UserRole.values()) {
            if (ur.getRole().equals(role)) {
                return ur;
            }
        }
        return null;
    }
}
