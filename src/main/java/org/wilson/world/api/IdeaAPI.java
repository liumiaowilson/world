package org.wilson.world.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

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
import org.wilson.world.event.Event;
import org.wilson.world.event.EventType;
import org.wilson.world.idea.IdeaIterator;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.IdeaManager;
import org.wilson.world.manager.MarkManager;
import org.wilson.world.manager.NotifyManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.manager.StarManager;
import org.wilson.world.manager.URLManager;
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
        name = name.trim();
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Idea content should be provided."));
        }
        content = content.trim();
        
        try {
            org.wilson.world.model.Idea idea = new org.wilson.world.model.Idea();
            idea.name = name;
            idea.content = content;
            IdeaManager.getInstance().createIdea(idea);
            
            Event event = new Event();
            event.type = EventType.CreateIdea;
            event.data.put("data", idea);
            EventManager.getInstance().fireEvent(event);
            
            StarManager.getInstance().reset();
            
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
        name = name.trim();
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Idea content should be provided."));
        }
        content = content.trim();
        
        try {
            Idea oldIdea = IdeaManager.getInstance().getIdea(id);
            
            org.wilson.world.model.Idea idea = new org.wilson.world.model.Idea();
            idea.id = id;
            idea.name = name;
            idea.content = content;
            IdeaManager.getInstance().updateIdea(idea);
            
            Event event = new Event();
            event.type = EventType.UpdateIdea;
            event.data.put("old_data", oldIdea);
            event.data.put("new_data", idea);
            EventManager.getInstance().fireEvent(event);
            
            StarManager.getInstance().postProcess(idea);
            
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
            Set<Integer> frozenIds = IdeaManager.getInstance().getFronzeTaskIds();
            for(org.wilson.world.model.Idea idea : ideas) {
                if(markedIds != null) {
                    String id = String.valueOf(idea.id);
                    if(markedIds.contains(id)) {
                        idea.marked = true;
                    }
                    else {
                        idea.marked = false;
                    }
                }
                
                if(frozenIds.contains(idea.id)) {
                    idea.frozen = true;
                }
                else {
                    idea.frozen = false;
                }
            }
            
            StarManager.getInstance().process(ideas);
            
            Collections.sort(ideas, new Comparator<org.wilson.world.model.Idea>(){
                @Override
                public int compare(org.wilson.world.model.Idea i1, org.wilson.world.model.Idea i2) {
                    if(i1.marked == i2.marked) {
                        if(i1.starred == i2.starred) {
                            return -(i1.id - i2.id);
                        }
                        else if(i1.starred && !i2.starred) {
                            return -1;
                        }
                        else {
                            return 1;
                        }
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
            Idea idea = IdeaManager.getInstance().getIdea(id);
            
            IdeaManager.getInstance().deleteIdea(id);
            
            Event event = new Event();
            event.type = EventType.DeleteIdea;
            event.data.put("data", idea);
            EventManager.getInstance().fireEvent(event);
            
            StarManager.getInstance().postProcess(idea);
            
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
                String name = obj.getString("name").trim();
                String content = obj.getString("content").trim();
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
            
            Idea oldIdea = IdeaManager.getInstance().getIdea(id);
            IdeaManager.getInstance().deleteIdea(id);
            
            for(Idea newIdea : newIdeas) {
                IdeaManager.getInstance().createIdea(newIdea);
            }
            
            Event event = new Event();
            event.type = EventType.SplitIdea;
            event.data.put("old_data", oldIdea);
            event.data.put("new_data", newIdeas);
            EventManager.getInstance().fireEvent(event);
            
            StarManager.getInstance().postProcess(oldIdea);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Idea has been successfully splitted."));
        }
        catch(Exception e) {
            logger.error("failed to split idea!", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Failed to split idea."));
        }
    }
    
    @POST
    @Path("/merge")
    @Produces("application/json")
    public Response merge(
            @FormParam("id") int id,
            @FormParam("ids") String ids,
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
        if(StringUtils.isBlank(ids)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Ideas to be merged are needed."));
        }
        if(StringUtils.isBlank(name)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("New idea name is needed."));
        }
        name = name.trim();
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("New idea content is needed."));
        }
        content = content.trim();
        
        String [] items = ids.trim().split(",");
        List<Integer> idList = new ArrayList<Integer>();
        for(String item : items) {
            try {
                int mergeId = Integer.parseInt(item);
                if(!idList.contains(mergeId)) {
                    idList.add(mergeId);
                }
            }
            catch(Exception e) {
                logger.error("failed to parse id", e);
            }
        }
        if(!idList.contains(id)) {
            idList.add(id);
        }
        
        try {
            Idea newIdea = new Idea();
            newIdea.name = name;
            newIdea.content = content;
            IdeaManager.getInstance().createIdea(newIdea);
            
            List<Idea> oldIdeas = new ArrayList<Idea>();
            for(int mergeId : idList) {
                Idea oldIdea = IdeaManager.getInstance().getIdea(mergeId);
                oldIdeas.add(oldIdea);
                IdeaManager.getInstance().deleteIdea(mergeId);
                MarkManager.getInstance().unmark(IdeaManager.NAME, String.valueOf(mergeId));
            }
            
            Event event = new Event();
            event.type = EventType.MergeIdea;
            event.data.put("old_data", oldIdeas);
            event.data.put("new_data", newIdea);
            EventManager.getInstance().fireEvent(event);
            
            StarManager.getInstance().postProcess(oldIdeas);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Ideas have been successfully merged."));
        }
        catch(Exception e) {
            logger.error("failed to merge ideas!", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Failed to merge ideas."));
        }
    }
    
    @POST
    @Path("/batch_create")
    @Produces("application/json")
    public Response batchCreate(
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
            List<org.wilson.world.model.Idea> newIdeas = new ArrayList<org.wilson.world.model.Idea>();
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
                org.wilson.world.model.Idea newIdea = new org.wilson.world.model.Idea();
                newIdea.name = name;
                newIdea.content = content;
                newIdeas.add(newIdea);
            }
            
            for(Idea newIdea : newIdeas) {
                IdeaManager.getInstance().createIdea(newIdea);
            }
            
            Event event = new Event();
            event.type = EventType.BatchCreateIdea;
            event.data.put("data", newIdeas);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Batch ideas have been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to batch create ideas!", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Failed to batch create ideas."));
        }
    }
    
    @GET
    @Path("/random")
    @Produces("application/json")
    public Response random(
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
            Idea idea = IdeaManager.getInstance().randomIdea();
            if(idea != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Random idea has been successfully fetched.");
                result.data = idea;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Random idea does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get random idea!", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Failed to batch create ideas."));
        }
    }
    
    @GET
    @Path("/prev")
    @Produces("application/json")
    public Response prev(
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
            List<Idea> ideas = IdeaManager.getInstance().getIdeas();
            int prev = -1;
            for(int i = 0; i < ideas.size(); i++) {
                Idea idea = ideas.get(i);
                if(idea.id == id) {
                    prev = i - 1;
                    break;
                }
            }
            
            if(prev >= 0) {
                prev = ideas.get(prev).id;
            }
            
            APIResult result = APIResultUtils.buildOKAPIResult("Previous idea has been successfully fetched.");
            result.data = prev;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get previous idea", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/next")
    @Produces("application/json")
    public Response next(
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
            List<Idea> ideas = IdeaManager.getInstance().getIdeas();
            int next = -1;
            for(int i = 0; i < ideas.size(); i++) {
                Idea idea = ideas.get(i);
                if(idea.id == id) {
                    next = i + 1;
                    break;
                }
            }
            
            if(next >= ideas.size()) {
                next = -1;
            }
            
            if(next >= 0) {
                next = ideas.get(next).id;
            }
            
            APIResult result = APIResultUtils.buildOKAPIResult("Next idea has been successfully fetched.");
            result.data = next;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get next idea", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/start_iterator")
    @Produces("application/json")
    public Response startIterator(
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
            String mode = NotifyManager.getInstance().getModeStatus();
            if(!StringUtils.isBlank(mode)) {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Cannot start idea iterator because of other existing mode."));
            }
            
            IdeaIterator.getInstance().setEnabled(true);
            String basePath = URLManager.getInstance().getBaseUrl();
            URLManager.getInstance().setCenterUrl(basePath + "/jsp/idea_edit.jsp?dir=next");
            APIResult result = APIResultUtils.buildOKAPIResult("Idea iterator has been successfully started.");
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to start iterator", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/stop_iterator")
    @Produces("application/json")
    public Response stopIterator(
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
            IdeaIterator.getInstance().setEnabled(false);
            URLManager.getInstance().setCenterUrl(null);
            APIResult result = APIResultUtils.buildOKAPIResult("Idea iterator has been successfully stopped.");
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to stop iterator", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
