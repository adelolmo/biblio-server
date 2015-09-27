package org.ado.biblio.auth;

import com.google.common.base.Optional;
import io.dropwizard.auth.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

/**
 * @author Andoni del Olmo
 * @since 27.09.15
 */
public class TokenAuthFactory<T> extends AuthFactory<TokenCredentials, T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenAuthFactory.class);
    private String _realm;
    private Class<T> _generatedClass;
    private UnauthorizedHandler _unauthorizedHandler = new DefaultUnauthorizedHandler();

    @Context
    private HttpServletRequest _request;

    public TokenAuthFactory(Authenticator<TokenCredentials, T> authenticator) {
        super(authenticator);
    }

    public TokenAuthFactory(Authenticator<TokenCredentials, T> authenticator, String realm, Class<T> generatedClass) {
        super(authenticator);
        _realm = realm;
        _generatedClass = generatedClass;
    }

    public TokenAuthFactory<T> responseBuilder(UnauthorizedHandler unauthorizedHandler) {
        _unauthorizedHandler = unauthorizedHandler;
        return this;
    }

    @Override
    public AuthFactory<TokenCredentials, T> clone(boolean required) {
        return new TokenAuthFactory<>(authenticator(), _realm, _generatedClass).responseBuilder(_unauthorizedHandler);
    }

    @Override
    public void setRequest(HttpServletRequest request) {
        _request = request;
    }

    @Override
    public T provide() {
        if (_request != null) {
            final String header = _request.getHeader(HttpHeaders.AUTHORIZATION);

            try {
                if (header != null) {
                    final TokenCredentials credentials = new TokenCredentials(header);
                    final Optional<T> result = authenticator().authenticate(credentials);
                    if (result.isPresent()) {
                        return result.get();
                    }
                }
            } catch (AuthenticationException e) {
                LOGGER.warn("Error authenticating credentials", e);
                throw new InternalServerErrorException();
            }
        }

        throw new WebApplicationException(_unauthorizedHandler.buildResponse("", _realm));
    }

    @Override
    public Class<T> getGeneratedClass() {
        return _generatedClass;
    }
}