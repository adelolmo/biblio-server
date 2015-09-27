package org.ado.biblio.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.validation.ValidationMethod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {

    public static final String SESSION_PARAM = "session";
    public static final String USERNAME_PARAM = "username";

    /** The Session ID or Token */
    @JsonProperty("session")
    private final String _session;
    
    /** Person ID - the owner of the Session */
    @JsonProperty("username")
    private String _username;

    
    /**
     * Empty constructor creates a new Session object with a generated
     * _session ID (aka token).
     */
    public Session() {
        _session = UUID.randomUUID().toString();
        _username = "";
    }
    
    /**
     * Creates a Session object with the specified ID, or token.
     * @param sessionToken
     */
    public Session(String sessionToken) {
        _session = sessionToken;
        _username = "";
    }
    
    /**
     * Validates the _session's token to proper UUID form.
     * @return A boolean value. If true the _session token is correctly formed, otherwise false.
     */
    @JsonIgnore
    @ValidationMethod(message="may not be correctly formatted token")
    public boolean isValidToken() {
        boolean isValid = true;
        try {
            UUID.fromString(_session);
        } catch (Exception e) {
            isValid = false;
        }
        return isValid;
    }
    
    /**
     * Returns the _session's token.
     * @return String containing the _session token value.
     */
    public String getSession() {
        return _session;
    }

    /**
     * @return the users _username
     */
    public String getUsername() {
        return _username;
    }

    /**
     * @param username the users username to set
     */
    public void setUsername(String username) {
        _username = username;
    }

    public Map<String, String> paramMap() {
        Map<String, String> params = new HashMap<>();
        params.put("username", _username);
        return params;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Session session = (Session) o;

        if (_session != null ? !_session.equals(session._session) : session._session != null) return false;
        return !(_username != null ? !_username.equals(session._username) : session._username != null);

    }

    @Override
    public int hashCode() {
        int result = _session != null ? _session.hashCode() : 0;
        result = 31 * result + (_username != null ? _username.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Session{");
        sb.append("_session='").append(_session).append('\'');
        sb.append(", _username='").append(_username).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
