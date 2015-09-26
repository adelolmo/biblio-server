package org.ado.biblio.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author Andoni del Olmo
 * @since 23.09.15
 */
@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LendResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(LendResource.class);


    // ----- STATIC ACCESSOR ---------------------------------------------------------------------------------------- //

    // ----- TO BE IMPLEMENTED BY SUBCLASSES ------------------------------------------------------------------------ //

    // ----- OVERWRITTEN METHODS ------------------------------------------------------------------------------------ //

    // ----- GETTERS / SETTER --------------------------------------------------------------------------------------- //

    // ----- INTERNAL HELPER ---------------------------------------------------------------------------------------- //

    // ----- HELPER CLASSES ----------------------------------------------------------------------------------------- //

    // ----- DEPENDENCY INJECTION ----------------------------------------------------------------------------------- //
}