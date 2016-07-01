package org.wilson.world.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

@Path("/demo")
public class Demo {
    private Logger logger = Logger.getLogger(Demo.class);
    
    @GET
    @Path("/msg")
    public Response message() {
        String msg = "Hello World";
        if(logger.isDebugEnabled()) {
            logger.debug("print demo message");
        }
        return Response.status(200).entity(msg).build();
    }
}
