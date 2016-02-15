package org.ado.biblio.error;

import io.dropwizard.jersey.errors.ErrorMessage;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {

    @Override
    public Response toResponse(final IllegalArgumentException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(new ErrorMessage(Response.Status.BAD_REQUEST.getStatusCode(), exception.getMessage()))
            .build();
    }

}
