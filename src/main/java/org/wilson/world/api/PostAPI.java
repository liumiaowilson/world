package org.wilson.world.api;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.api.util.APIResultUtils;
import org.wilson.world.event.Event;
import org.wilson.world.event.EventType;
import org.wilson.world.manager.DataManager;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.IdeaManager;
import org.wilson.world.manager.PostManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.Idea;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Path("/post")
public class PostAPI {
    private static final Logger logger = Logger.getLogger(PostAPI.class);
    
    @POST
    @Path("/send")
    @Produces("application/json")
    public Response send(
            @FormParam("key") String key,
            @FormParam("post") String post,
            @Context HttpHeaders headers,
            @Context HttpServletRequest request,
            @Context UriInfo uriInfo) throws URISyntaxException {
        String k = DataManager.getInstance().getValue("public.key");
        if(k == null || !k.equals(key)) {
            return APIResultUtils.buildURLResponse(request, "public_error.jsp");
        }
        
        if(post != null) {
            post = post.trim();
            String [] items = post.split("\n");
            for(String item : items) {
                PostManager.getInstance().addPost(item);
            }
        }
        return APIResultUtils.buildURLResponse(request, "public/post.jsp");
    }
    
    @POST
    @Path("/process")
    @Produces("application/json")
    public Response process(
            @FormParam("ideas") String ideas,
            @QueryParam("token") String token,
            @Context HttpHeaders headers,
            @Context HttpServletRequest request,
            @Context UriInfo uriInfo) {
        String user_token = token;
        if(StringUtils.isBlank(user_token)) {
            user_token = (String)request.getSession().getAttribute("world-token");
        }
        if(!SecManager.getInstance().isValidToken(user_token)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Authentication is needed."));
        }
        if(StringUtils.isBlank(ideas)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Ideas are needed."));
        }
        
        try {
            List<Idea> newIdeas = new ArrayList<Idea>();
            JSONArray json = JSONArray.fromObject(ideas);
            for(int i = 0; i < json.size(); i++) {
                JSONObject obj = json.getJSONObject(i);
                String name = obj.getString("name").trim();
                String content = obj.getString("content").trim();
                if(StringUtils.isBlank(name)) {
                    return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Idea name is needed."));
                }
                if(StringUtils.isBlank(content)) {
                    content = name;
                }
                Idea newIdea = new Idea();
                newIdea.name = name;
                newIdea.content = content;
                newIdeas.add(newIdea);
            }
            
            for(Idea newIdea : newIdeas) {
                IdeaManager.getInstance().createIdea(newIdea);
            }
            
            PostManager.getInstance().clear();
            PostManager.getInstance().removeAlert();
            
            Event event = new Event();
            event.type = EventType.PostProcessIdea;
            event.data.put("data", newIdeas);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Post process ideas have been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to post process ideas!", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Failed to post process ideas."));
        }
    }
}
