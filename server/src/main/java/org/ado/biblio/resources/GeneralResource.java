package org.ado.biblio.resources;

import org.slf4j.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * @author Andoni del Olmo
 * @since 21.09.15
 */
public class GeneralResource {

    protected void formatAndThrow(Logger logger, Response.Status status, String message) {
        logger.error("response status {} message \"{}\".", status, message);
        throw new WebApplicationException(Response.status(status).entity("{\"error\":\"" + message + "\"}").build());
    }
}