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

    private final UserDao _userDao;
    private final SessionDao _sessionDao;

    public UserAuthenticator(UserDao userDao, SessionDao sessionDao) {
        _userDao = userDao;
        _sessionDao = sessionDao;
    }

    @Override
    public Optional<User> authenticate(final TokenCredentials credentials) throws AuthenticationException {
        final String sessionToken = credentials.getSessionToken();
        final Session session = _sessionDao.lookupSession(new Session(sessionToken));
        if (session != null) {
            return _userDao.findByUsername(session.getUsername());
        }
        LOGGER.debug("wrong credentials. no user found for token: {}", sessionToken);
        return Optional.absent();
    }

}