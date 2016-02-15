package org.ado.biblio.error;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class AuthenticationErrorMapper implements ExceptionMapper<AuthenticationErrorException> {

    @Override
    public Response toResponse(AuthenticationErrorException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(new ErrorMessage(Response.Status.BAD_REQUEST.getStatusCode(), exception.getMessage()))
            .build();
    }
}