package org.wilson.world.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
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
import org.wilson.world.manager.IdeaManager;
import org.wilson.world.manager.MarkManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.Idea;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Path("idea")
public class IdeaAPI {
    private static final Logger logger = Logger.getLogger(IdeaAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("content") String content,
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
        
        if(StringUtils.isBlank(name)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Idea name should be provided."));
        }
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Idea content should be provided."));
        }
        
        try {
            org.wilson.world.model.Idea idea = new org.wilson.world.model.Idea();
            idea.name = name;
            idea.content = content;
            IdeaManager.getInstance().createIdea(idea);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Idea has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create idea", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("name") String name, 
            @FormParam("content") String content,
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
        
        if(StringUtils.isBlank(name)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Idea name should be provided."));
        }
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Idea content should be provided."));
        }
        
        try {
            org.wilson.world.model.Idea idea = new org.wilson.world.model.Idea();
            idea.id = id;
            idea.name = name;
            idea.content = content;
            IdeaManager.getInstance().updateIdea(idea);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Idea has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update idea", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/get")
    @Produces("application/json")
    public Response get(
            @QueryParam("id") int id,
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
        
        try {
            org.wilson.world.model.Idea idea = IdeaManager.getInstance().getIdea(id);
            if(idea != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Idea has been successfully fetched.");
                result.data = idea;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Idea does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get idea", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/list")
    @Produces("application/json")
    public Response list(
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
        
        try {
            List<org.wilson.world.model.Idea> ideas = IdeaManager.getInstance().getIdeas();
            List<String> markedIds = MarkManager.getInstance().getMarked(IdeaManager.NAME);
            if(markedIds != null) {
                for(org.wilson.world.model.Idea idea : ideas) {
                    String id = String.valueOf(idea.id);
                    if(markedIds.contains(id)) {
                        idea.marked = true;
                    }
                    else {
                        idea.marked = false;
                    }
                }
            }
            
            Collections.sort(ideas, new Comparator<org.wilson.world.model.Idea>(){
                @Override
                public int compare(org.wilson.world.model.Idea i1, org.wilson.world.model.Idea i2) {
                    if(i1.marked == i2.marked) {
                        return -(i1.id - i2.id);
                    }
                    else if(i1.marked && !i2.marked) {
                        return -1;
                    }
                    else {
                        return 1;
                    }
                }
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("Ideas have been successfully fetched.");
            result.list = ideas;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get ideas", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/delete")
    @Produces("application/json")
    public Response delete(
            @QueryParam("id") int id,
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
        
        try {
            IdeaManager.getInstance().deleteIdea(id);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Idea has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete idea", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/split")
    @Produces("application/json")
    public Response split(
            @FormParam("id") int id,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("New ideas are needed."));
        }
        
        try {
            List<org.wilson.world.model.Idea> newIdeas = new ArrayList<org.wilson.world.model.Idea>();
            JSONArray json = JSONArray.fromObject(ideas);
            for(int i = 0; i < json.size(); i++) {
                JSONObject obj = json.getJSONObject(i);
                String name = obj.getString("name");
                String content = obj.getString("content");
                if(StringUtils.isBlank(name)) {
                    return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("New idea name is needed."));
                }
                if(StringUtils.isBlank(content)) {
                    content = name;
                }
                org.wilson.world.model.Idea newIdea = new org.wilson.world.model.Idea();
                newIdea.name = name;
                newIdea.content = content;
                newIdeas.add(newIdea);
            }
            
            IdeaManager.getInstance().deleteIdea(id);
            
            for(Idea newIdea : newIdeas) {
                IdeaManager.getInstance().createIdea(newIdea);
            }
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Idea has been successfully splitted."));
        }
        catch(Exception e) {
            logger.error("failed to split idea!", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Failed to split idea."));
        }
    }
}
