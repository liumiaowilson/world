package org.wilson.world.api;

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
import org.wilson.world.event.Event;
import org.wilson.world.event.EventType;
import org.wilson.world.manager.ActionManager;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.MarkManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.Action;

@Path("action")
public class ActionAPI {
    private static final Logger logger = Logger.getLogger(ActionAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("script") String script,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Action name should be provided."));
        }
        if(StringUtils.isBlank(script)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Action script should be provided."));
        }
        
        try {
            Action action = new Action();
            action.name = name;
            action.script = script;
            ActionManager.getInstance().createAction(action);
            
            Event event = new Event();
            event.type = EventType.CreateAction;
            event.data.put("data", action);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Action has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create action", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("name") String name, 
            @FormParam("script") String script,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Action name should be provided."));
        }
        if(StringUtils.isBlank(script)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Action script should be provided."));
        }
        
        try {
            Action oldAction = ActionManager.getInstance().getAction(id);
            
            Action action = new Action();
            action.id = id;
            action.name = name;
            action.script = script;
            ActionManager.getInstance().updateAction(action);
            
            Event event = new Event();
            event.type = EventType.UpdateAction;
            event.data.put("old_data", oldAction);
            event.data.put("new_data", action);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Action has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update action", e);
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
            Action action = ActionManager.getInstance().getAction(id);
            if(action != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Action has been successfully fetched.");
                result.data = action;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Action does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get action", e);
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
            List<Action> actions = ActionManager.getInstance().getActions();
            
            List<String> markedIds = MarkManager.getInstance().getMarked(ActionManager.NAME);
            if(markedIds != null) {
                for(Action action : actions) {
                    String id = String.valueOf(action.id);
                    if(markedIds.contains(id)) {
                        action.marked = true;
                    }
                    else {
                        action.marked = false;
                    }
                }
            }
            
            Collections.sort(actions, new Comparator<Action>(){
                @Override
                public int compare(Action a1, Action a2) {
                    if(a1.marked == a2.marked) {
                        return -(a1.id - a2.id);
                    }
                    else if(a1.marked && !a2.marked) {
                        return -1;
                    }
                    else {
                        return 1;
                    }
                }
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("Actions have been successfully fetched.");
            result.list = actions;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get actions", e);
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
            Action action = ActionManager.getInstance().getAction(id);
            
            ActionManager.getInstance().deleteAction(id);
            
            Event event = new Event();
            event.type = EventType.DeleteAction;
            event.data.put("data", action);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Action has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete action", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
