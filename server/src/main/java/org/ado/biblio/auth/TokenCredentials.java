package org.ado.biblio.auth;

/**
 * @author Andoni del Olmo
 * @since 27.09.15
 */
public class TokenCredentials {

    private String _sessionToken;

    public TokenCredentials(String sessionToken) {
        _sessionToken = sessionToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TokenCredentials that = (TokenCredentials) o;

        return !(_sessionToken != null ? !_sessionToken.equals(that._sessionToken) : that._sessionToken != null);

    }

    @Override
    public int hashCode() {
        return _sessionToken != null ? _sessionToken.hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TokenCredentials{");
        sb.append("_sessionToken='").append(_sessionToken).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getSessionToken() {
        return _sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        _sessionToken = sessionToken;
    }
}