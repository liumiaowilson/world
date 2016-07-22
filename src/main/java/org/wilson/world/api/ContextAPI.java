package org.wilson.world.api;

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
import org.wilson.world.manager.ContextManager;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.manager.StarManager;
import org.wilson.world.model.APIResult;

@Path("context")
public class ContextAPI {
    private static final Logger logger = Logger.getLogger(ContextAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("color") String color,
            @FormParam("description") String description,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Context name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(color)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Context color should be provided."));
        }
        color = color.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Context description should be provided."));
        }
        description = description.trim();
        
        try {
            org.wilson.world.model.Context context = new org.wilson.world.model.Context();
            context.name = name;
            context.color = color;
            context.description = description;
            ContextManager.getInstance().createContext(context);
            
            Event event = new Event();
            event.type = EventType.CreateContext;
            event.data.put("data", context);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Context has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create context", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("name") String name, 
            @FormParam("color") String color,
            @FormParam("description") String description,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Context name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(color)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Context color should be provided."));
        }
        color = color.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Context description should be provided."));
        }
        description = description.trim();
        
        try {
            org.wilson.world.model.Context oldContext = ContextManager.getInstance().getContext(id);
            
            org.wilson.world.model.Context context = new org.wilson.world.model.Context();
            context.id = id;
            context.name = name;
            context.color = color;
            context.description = description;
            ContextManager.getInstance().updateContext(context);
            
            Event event = new Event();
            event.type = EventType.UpdateContext;
            event.data.put("old_data", oldContext);
            event.data.put("new_data", context);
            EventManager.getInstance().fireEvent(event);
            
            StarManager.getInstance().postProcess(context);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Context has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update context", e);
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
            org.wilson.world.model.Context context = ContextManager.getInstance().getContext(id);
            if(context != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Context has been successfully fetched.");
                result.data = context;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Context does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get context", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/set_current")
    @Produces("application/json")
    public Response setCurrent(
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
            org.wilson.world.model.Context context = ContextManager.getInstance().getContext(id);
            if(context != null) {
                ContextManager.getInstance().setCurrentContext(context);
                APIResult result = APIResultUtils.buildOKAPIResult("Context has been successfully set to current.");
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Context does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to set current context", e);
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
            List<org.wilson.world.model.Context> contexts = ContextManager.getInstance().getContexts();
            
            APIResult result = APIResultUtils.buildOKAPIResult("Contexts have been successfully fetched.");
            result.list = contexts;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get contexts", e);
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
            org.wilson.world.model.Context context = ContextManager.getInstance().getContext(id);
            
            if(ContextManager.getInstance().isContextUsed(context.name)) {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Context is being used."));
            }
            
            ContextManager.getInstance().deleteContext(id);
            
            Event event = new Event();
            event.type = EventType.DeleteContext;
            event.data.put("data", context);
            EventManager.getInstance().fireEvent(event);
            
            StarManager.getInstance().postProcess(context);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Context has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete context", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
