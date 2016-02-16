package org.ado.biblio.error;

import io.dropwizard.jersey.errors.ErrorMessage;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {

    @Override
    public Response toResponse(final IllegalArgumentException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(
                        new ErrorMessage(
                                Response.Status.BAD_REQUEST.getStatusCode(),
                                StringUtils.isEmpty(exception.getMessage()) ? Response.Status.BAD_REQUEST.toString() : exception.getMessage()))
                .build();
    }

}
