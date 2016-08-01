package org.wilson.world.api;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.wilson.world.manager.TaskTagManager;

@Path("/task_tag")
public class TaskTagAPI {
    @GET
    @Path("/tags")
    @Produces("application/json")
    public Response tags(
            @Context HttpHeaders headers,
            @Context HttpServletRequest request,
            @Context UriInfo uriInfo) {
        StringBuffer sb = new StringBuffer("[");
        List<String> tags = TaskTagManager.getInstance().getTagNames();
        for(int i = 0; i < tags.size(); i++) {
            sb.append("\"" + tags.get(i) + "\"");
            if(i != tags.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        
        return Response.status(200).type(MediaType.APPLICATION_JSON).entity(sb.toString()).build();
    }
}
