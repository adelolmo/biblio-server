package org.ado.biblio.auth;

import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import org.ado.biblio.core.Session;
import org.ado.biblio.db.SessionDao;
import org.ado.biblio.db.UserDao;
import org.ado.biblio.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andoni del Olmo
 * @since 26.09.15
 */
public class UserAuthenticator implements Authenticator<TokenCredentials, User> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAuthenticator.class);

    private UserDao _userDao;
    private SessionDao _sessionDao;

    public UserAuthenticator(UserDao userDao, SessionDao sessionDao) {
        _userDao = userDao;
        _sessionDao = sessionDao;
    }

    @Override
    public Optional<User> authenticate(TokenCredentials credentials) throws AuthenticationException {
/*        final String sessionToken = c.getRequest().getHeaderValue(HttpHeaders.AUTHORIZATION);
        if (sessionToken == null) {
            ResponseException.formatAndThrow(Response.Status.UNAUTHORIZED, "Authorization header was not set");
        }*/

        final String sessionToken = credentials.getSessionToken();
        final Session session = _sessionDao.lookupSession(new Session(sessionToken));

        final User user = _userDao.findByUsername(session.getUsername());
        if (user != null) {
            return Optional.of(user);
        }
//        if ("secret".equals(credentials.getPassword())) {
//            final User user = new User();
//            user.setUsername(credentials.getUsername());
//            return Optional.of(user);
//        }
        return Optional.absent();
    }

}