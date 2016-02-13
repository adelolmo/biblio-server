package org.ado.biblio.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import io.dropwizard.auth.AuthFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.ado.biblio.auth.TokenAuthFactory;
import org.ado.biblio.auth.UserAuthenticator;
import org.ado.biblio.core.Session;
import org.ado.biblio.db.BookDao;
import org.ado.biblio.db.SessionDao;
import org.ado.biblio.db.UserDao;
import org.ado.biblio.model.Book;
import org.ado.biblio.model.User;
import org.ado.biblio.model.UserRole;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.hibernate.HibernateException;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Andoni del Olmo
 * @since 12.10.15
 */
public class BookResourceTest {

    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    private static BookDao _bookDaoMock = mock(BookDao.class);
    private static UserDao _userDaoMock = mock(UserDao.class);
    private static SessionDao _sessionDaoMock = mock(SessionDao.class);
    private static HttpServletRequest _requestMock = mock(HttpServletRequest.class);

    @ClassRule
    public static ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new BookResource(_bookDaoMock))
            .setTestContainerFactory(new GrizzlyWebTestContainerFactory())
            .addProvider(AuthFactory.binder(getAuth()))
            .addProvider(new RuntimeExceptionMapper())
            .build();

    @Before
    public void setUp() throws Exception {
//        MockitoAnnotations.initMocks(this);
        when(_requestMock.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("auth token");
        final Session session = new Session();
        session.setUsername("username");
        when(_sessionDaoMock.lookupSession(any(Session.class))).thenReturn(session);
        final User user = new User();
        user.setUsername("username");
        user.setPassword("passwordHashed");
        user.setRole(UserRole.USER);
        when(_userDaoMock.findByUsername("username")).thenReturn(Optional.of(user));
    }

    @After
    public void tearDown() throws Exception {
        reset(_requestMock, _bookDaoMock, _sessionDaoMock, _userDaoMock);
    }

    @Test
    public void shouldNotBeAuthenticated() throws Exception {
        when(_sessionDaoMock.lookupSession(any(Session.class))).thenThrow(new WebApplicationException("opss"));

        final Response response = resources.getJerseyTest().target("/books").request()
                .header(HttpHeaders.AUTHORIZATION, "nothing")
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(createBook()));

        assertEquals("response", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void shouldCreateBook() throws Exception {
        final Book book = new Book();
        book.setId(1L);
        when(_bookDaoMock.save(any(Book.class))).thenReturn(book);

        final Response response = resources.getJerseyTest().target("/books").request()
                .header(HttpHeaders.AUTHORIZATION, "auth token")
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(createBook()));

        assertEquals("response", Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals("location", "http://localhost:9998/books/1", response.getLocation().toString());
    }

    @Test
    public void shouldRejectConflictedBook() throws Exception {
        when(_bookDaoMock.save(any(Book.class))).thenThrow(new HibernateException("duplicated book"));

        final Response response = resources.getJerseyTest().target("/books").request()
                .header(HttpHeaders.AUTHORIZATION, "auth token")
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(createBook()));

        assertEquals("response", Response.Status.CONFLICT.getStatusCode(), response.getStatus());
//        assertEquals("error", "error", response.readEntity(String.class));
    }

    @Test
    public void shouldGetBook() {
        when(_bookDaoMock.findById(any(User.class), eq(1L))).thenReturn(createBook());

        final Response response = resources.getJerseyTest().target("/books/1").request()
                .header(HttpHeaders.AUTHORIZATION, "auth token")
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertEquals("response", Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("book", createBook(), response.readEntity(Book.class));
    }

    @Test
    public void shouldNotGetBook() {
        when(_bookDaoMock.findById(any(User.class), eq(1L))).thenReturn(null);

        final Response response = resources.getJerseyTest().target("/books/1").request()
                .header(HttpHeaders.AUTHORIZATION, "auth token")
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertEquals("response", Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void shouldGetAllBooks() throws JsonProcessingException {
        when(_bookDaoMock.findAll(any(User.class))).thenReturn(Collections.singletonList(createBook()));

        final Response response = resources.getJerseyTest().target("/books").request()
                .header(HttpHeaders.AUTHORIZATION, "auth token")
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertEquals("response", Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("books", MAPPER.writeValueAsString(Collections.singletonList(createBook())), response.readEntity(String.class));
    }

    @Test
    public void shouldUpdateBook(){

    }

    private Book createBook() {
        final Book book = new Book();
        book.setAuthor("author");
        book.setTitle("title");
        book.setIsbn("isbn");
        return book;
    }

    private static TokenAuthFactory<User> getAuth() {
        try {
            final TokenAuthFactory<User> authFactory = new TokenAuthFactory<User>(new UserAuthenticator(_userDaoMock, _sessionDaoMock), "SUPER SECRET STUFF", User.class);
            authFactory.setRequest(_requestMock);
            return authFactory;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class RuntimeExceptionMapper implements ExceptionMapper<WebApplicationException> {
        @Override
        public Response toResponse(WebApplicationException exception) {
            return Response.serverError()
                    .type(MediaType.TEXT_PLAIN_TYPE)
                    .entity(exception.getMessage())
                    .build();
        }
    }
}