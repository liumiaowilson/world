package org.wilson.world;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/demo")
public class Demo {
    @GET
    public Response message() {
        String msg = "Hello World";
        return Response.status(200).entity(msg).build();
    }
}
