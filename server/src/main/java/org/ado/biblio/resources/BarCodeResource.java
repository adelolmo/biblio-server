package org.ado.biblio.resources;

import com.codahale.metrics.annotation.Timed;
import io.dropwizard.hibernate.UnitOfWork;
import org.ado.biblio.db.BookDao;
import org.ado.biblio.model.BarCode;
import org.ado.biblio.model.Book;
import org.ado.googleapis.books.BookInfo;
import org.ado.googleapis.books.BookInfoLoader;
import org.ado.googleapis.books.NoBookInfoFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.util.Date;

/**
 * @author Andoni del Olmo
 * @since 21.09.15
 */
@Path("/barcode")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BarCodeResource extends GeneralResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(BarCodeResource.class);
    private BookDao _bookDao;
    private BookInfoLoader _bookInfoLoader;

    public BarCodeResource(BookDao bookDao) {
        _bookDao = bookDao;
        _bookInfoLoader = new BookInfoLoader();
    }

    @POST
    @Timed
    @UnitOfWork
//    @Path("/{id}")
    public Response addBarcode(@Valid BarCode barCode) {
        try {
            if (_bookDao.findByIsbn(barCode.getIsbn()) != null) {
                formatAndThrow(LOGGER, Response.Status.CONFLICT, String.format("Book with isbn %s already exists", barCode.getIsbn()));
            }

            final BookInfo bookInfo = _bookInfoLoader.getBookInfo(barCode.getIsbn());
            final Book book = _bookDao.save(getBook(bookInfo));

            String uri = String.format("/books/%d", book.getId());
            return Response.created(URI.create(uri)).entity(book).build();

        } catch (IOException e) {
            formatAndThrow(LOGGER, Response.Status.INTERNAL_SERVER_ERROR, "Unable to process barcode");
        } catch (NoBookInfoFoundException e) {
            formatAndThrow(LOGGER, Response.Status.NOT_FOUND, String.format("Book not found with isbn %s", barCode.getIsbn()));
        }
        return null;
    }

    private Book getBook(BookInfo bookInfo) {
        final Book book = new Book();
        book.setTitle(bookInfo.getTitle());
        book.setAuthor(bookInfo.getAuthor());
        book.setIsbn(bookInfo.getIsbn());
        book.setCtime(new Date());
        book.setImageUrl(bookInfo.getThumbnailUrl());
        return book;
    }
}