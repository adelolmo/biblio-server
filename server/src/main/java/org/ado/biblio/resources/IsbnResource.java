package org.ado.biblio.resources;

import com.codahale.metrics.annotation.Timed;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import org.ado.biblio.db.BookDao;
import org.ado.biblio.model.Book;
import org.ado.biblio.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Andoni del Olmo
 * @since 26.09.15
 */
@Path("/isbns")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IsbnResource extends GeneralResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(IsbnResource.class);
    private BookDao _bookDao;

    public IsbnResource(BookDao bookDao) {
        _bookDao = bookDao;
    }

    @GET
    @Timed
    @UnitOfWork
    @Path("/{isbn}")
    public Response findBookByIsbn(@Auth User user, @PathParam("isbn") String isbn) {
        final Book book = _bookDao.findByIsbn(user, isbn);
        if (book == null) {
            formatAndThrow(LOGGER, Response.Status.NOT_FOUND, String.format("Book not found with isbn %s", isbn));
        }
        return Response.ok().entity(book).build();
    }
}