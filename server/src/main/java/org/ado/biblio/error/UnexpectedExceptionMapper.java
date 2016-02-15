package org.ado.biblio.error;

import io.dropwizard.jersey.errors.ErrorMessage;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class UnexpectedExceptionMapper implements ExceptionMapper<UnexpectedException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnexpectedExceptionMapper.class);

    private final IdGenerator idGenerator;

    public UnexpectedExceptionMapper(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public Response toResponse(final UnexpectedException ex) {
        final long id = idGenerator.random();
        LOGGER.error(String.format("requestId=%016x, actionGroup=BUG, action=%s, %s", id, ex.action(), ex.message()));
        return Response.serverError().entity(new ErrorMessage(formatErrorMessage(id))).build();
    }

    private String formatErrorMessage(long id) {
        return String.format("There was an error processing your request. It has been logged (ID %016x).", id);
    }

}
