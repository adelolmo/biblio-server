package org.ado.biblio.resources;


import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import org.ado.biblio.core.Session;
import org.ado.biblio.db.SessionDao;
import org.ado.biblio.db.UserDao;
import org.ado.biblio.model.User;
import org.ado.biblio.model.UserRole;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.security.SecureRandom;
import java.util.Random;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource extends GeneralResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserResource.class);

    private final UserDao _userDao;
    private final SessionDao _sessionDao;

    public UserResource(UserDao userDao, SessionDao sessionDao) {
        _userDao = userDao;
        _sessionDao = sessionDao;
    }

    @POST
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public Response post(@Valid User user) {
        final User existingUser = _userDao.findByUsername(user.getUsername());
        if (existingUser == null) {
            final Random r = new SecureRandom();
            byte[] saltBytes = new byte[32];
            r.nextBytes(saltBytes);
            final String salt = DigestUtils.sha256Hex(saltBytes);
            user.setSalt(salt);
            user.setPassword(DigestUtils.sha256Hex(salt + user.getPassword()));
            user.setRole(UserRole.USER);

            _userDao.save(user);
        } else {
            final String sentPasswordHashed = DigestUtils.sha256Hex(existingUser.getSalt() + user.getPassword());
            if (!sentPasswordHashed.equals(existingUser.getPassword())) {
                formatAndThrow(LOGGER, Response.Status.BAD_REQUEST, "Invalid username / password");
            }
            user = existingUser;
        }

        final Session session = _sessionDao.createSession(user.getUsername());
        return Response.created(
                UriBuilder.fromResource(UserResource.class)
                        .path("/{id}")
                        .resolveTemplate("id", user.getId())
                        .build())
                .header("Session-Token", session.getSession())
                .build();
    }

    @PUT
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public User put(@Auth User existingUser, User user) {
        if (user.getPassword() != null) {
            Random r = new SecureRandom();
            byte[] saltBytes = new byte[32];
            r.nextBytes(saltBytes);
            String salt = DigestUtils.sha256Hex(saltBytes);
            existingUser.setSalt(salt);
            String password = user.getPassword();
            String hashedPassword = DigestUtils.sha256Hex(salt + password);
            existingUser.setPassword(hashedPassword);
        }
        if (user.getRole() != null) {
            existingUser.setRole(user.getRole());
        }
        if (user.getUsername() != null) {
            formatAndThrow(LOGGER, Response.Status.BAD_REQUEST, "You cannot update username");
        }
        _userDao.save(existingUser);

        return existingUser;
    }

    @GET
    @Path("/{id}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public User get(@Auth User existingUser, @PathParam("id") Long id) {
        return existingUser;
    }

    @DELETE
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public String delete(@Auth User existingUser, @HeaderParam("Authorization") String sessionToken) {
        if (existingUser.getUsername().equals("admin")) {
            formatAndThrow(LOGGER, Response.Status.BAD_REQUEST, "You cannot delete the admin user");
        }
        _userDao.delete(existingUser);
        Session session = new Session(sessionToken);
        session.setUsername(existingUser.getUsername());
        _sessionDao.deleteSession(session);
        return "{}";
    }

    @OPTIONS
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public void options() {
    }
}
