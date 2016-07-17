package org.wilson.world.api;

import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.wilson.world.api.util.APIResultUtils;
import org.wilson.world.manager.PostManager;

@Path("/post")
public class PostAPI {
    @POST
    @Path("/send")
    @Produces("application/json")
    public Response execute(
            @FormParam("post") String post,
            @Context HttpHeaders headers,
            @Context HttpServletRequest request,
            @Context UriInfo uriInfo) throws URISyntaxException {
        if(post != null) {
            post = post.trim();
            String [] items = post.split("\n");
            for(String item : items) {
                PostManager.getInstance().addPost(item);
            }
        }
        return APIResultUtils.buildURLResponse(request, "post.jsp");
    }
}
