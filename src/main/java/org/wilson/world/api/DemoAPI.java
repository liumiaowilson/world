package org.wilson.world.api;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/demo")
public class DemoAPI {
    @GET
    @Path("/names")
    @Produces("application/json")
    public Response names(
            @Context HttpHeaders headers,
            @Context HttpServletRequest request,
            @Context UriInfo uriInfo) {
        String content = "[\"wilson\", \"coco\"]";
        
        return Response.status(200).type(MediaType.APPLICATION_JSON).entity(content).build();
    }
}
