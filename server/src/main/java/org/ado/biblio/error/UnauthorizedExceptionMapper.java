package org.ado.biblio.error;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * @author Andoni del Olmo
 * @since 16.02.16
 */
public class UnauthorizedExceptionMapper implements ExceptionMapper<UnauthorizedException> {


    @Override
    public Response toResponse(UnauthorizedException exception) {
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(new ErrorMessage(Response.Status.UNAUTHORIZED.getStatusCode(), exception.getMessage()))
                .build();
    }
}