package org.ado.biblio.resources;

import com.codahale.metrics.annotation.Timed;
import io.dropwizard.hibernate.UnitOfWork;
import org.ado.biblio.db.BookDao;
import org.ado.biblio.model.Book;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Andoni del Olmo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

/**
 * @author Andoni del Olmo
 * @since 16.09.15
 */
@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookResource.class);

    private BookDao _bookDao;

    public BookResource(BookDao bookDao) {
        _bookDao = bookDao;
    }

    @POST
    @Timed
    @UnitOfWork
    @Path("/{id}")
    public Response createBook(@PathParam("id") String id,
                               @Valid Book book) {
        book.setId(id);
        _bookDao.save(book);

        String uri = String.format("/books/%s", id);
        return Response.created(URI.create(uri)).build();
    }

    @GET
    @Timed
    @UnitOfWork
    @Path("/{id}")
    public Response readBook(@PathParam("id") String id) {
        final Book book = _bookDao.findById(id);
        if (book == null) {
            formatAndThrow(Response.Status.NOT_FOUND, String.format("Book not found with id %s", id));
        }
        return Response.ok().entity(book).build();
    }

    @GET
    @Timed
    @UnitOfWork
    public Response readBooks() {
        final List<Book> books = _bookDao.findAll();
        return Response.ok().entity(books).build();
    }

    @PUT
    @Timed
    @UnitOfWork
    @Path("/{id}")
    public Response updateBook(@PathParam("id") String id,
                               Book book) {
        final Book actualBook = _bookDao.findById(id);
        if (actualBook == null) {
            formatAndThrow(Response.Status.NOT_FOUND, String.format("Book not found with id %s", id));
        }

        if (StringUtils.isNotBlank(book.getCode())) {
            actualBook.setCode(book.getCode());
        }
        if (StringUtils.isNotBlank(book.getFormat())) {
            actualBook.setFormat(book.getFormat());
        }

        _bookDao.save(actualBook);

        String uri = String.format("/books/%s", id);
        return Response.ok().location(URI.create(uri)).build();
    }

    @DELETE
    @Timed
    @UnitOfWork
    @Path("/{id}")
    public Response deleteBook(@PathParam("id") String id) {
        final Book book = _bookDao.findById(id);
        if (book == null) {
            formatAndThrow(Response.Status.NOT_FOUND, String.format("Book not found with id %s", id));
        } else {
            _bookDao.delete(book);
        }
        return Response.ok().build();
    }

    private void formatAndThrow(Response.Status status, String message) {
        throw new WebApplicationException(Response.status(status).entity("{\"error\":\"" + message + "\"}").build());
    }
}