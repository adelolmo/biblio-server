package org.ado.biblio.resources;


import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import org.ado.biblio.core.Session;
import org.ado.biblio.db.SessionDao;
import org.ado.biblio.db.UserDao;
import org.ado.biblio.encode.PasswordEncoder;
import org.ado.biblio.encode.PasswordHashed;
import org.ado.biblio.error.AuthenticationErrorException;
import org.ado.biblio.model.User;
import org.ado.biblio.model.UserRole;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource extends GeneralResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserResource.class);

    private final UserDao _userDao;
    private final SessionDao _sessionDao;

    public UserResource(final UserDao userDao, final SessionDao sessionDao) {
        _userDao = userDao;
        _sessionDao = sessionDao;
    }

    @POST
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public Response post(@Valid @NotNull final CreateUserRequest request) {
        if (request == null) {
            throw new IllegalArgumentException();
        }
        final Optional<User> existingUser = _userDao.findByUsername(request.username());
        if (!existingUser.isPresent()) {
            final PasswordHashed passwordHashed = PasswordEncoder.encode(request.password());
            final User user = new User(request.username(),
                    passwordHashed.salt(),
                    passwordHashed.encodedPassword(),
                    UserRole.USER);

            _userDao.save(user);

            return Response.created(
                    UriBuilder.fromResource(UserResource.class)
                            .path("/{id}")
                            .resolveTemplate("id", user.getId())
                            .build())
                    .header("Session-Token",
                            _sessionDao.createSession(user.getUsername()).getSession())
                    .build();
        } else {
            final String sentPasswordHashed =
                    DigestUtils.sha256Hex(existingUser.get().getSalt() + request.password());
            if (!sentPasswordHashed.equals(existingUser.get().getPassword())) {
                throw new AuthenticationErrorException();
            }
            return Response.noContent()
                    .header("Session-Token",
                            _sessionDao.createSession(existingUser.get().getUsername()).getSession())
                    .build();
        }
    }

    @PUT
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public User put(@Auth User existingUser, User user) {
        if (user.getPassword() != null) {
            final PasswordHashed passwordHashed = PasswordEncoder.encode(user.getPassword());
            existingUser.setSalt(passwordHashed.salt());
            existingUser.setPassword(passwordHashed.encodedPassword());
        }
        if (user.getRole() != null) {
            existingUser.setRole(user.getRole());
        }
        if (user.getUsername() != null) {
            throw new IllegalArgumentException("You cannot update username");
        }
        _userDao.save(existingUser);

        return existingUser;
    }

    @GET
    @Path("/{id}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public Response get(@Auth final User existingUser, @PathParam("id") final Long id) {
        if (!existingUser.getId().equals(id)) {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity("Unauthorized access to another user.")
                    .build();
        }
        return Response.ok().entity(existingUser).build();
    }

    @DELETE
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public String delete(@Auth User existingUser, @HeaderParam("Authorization") String sessionToken) {
        if (existingUser.getUsername().equals("admin")) {
            throw new IllegalArgumentException("You cannot delete the admin user");
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
