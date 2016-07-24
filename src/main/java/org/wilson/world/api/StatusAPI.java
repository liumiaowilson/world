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
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.manager.StatusManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.Status;

@Path("status")
public class StatusAPI {
    private static final Logger logger = Logger.getLogger(StatusAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("icon") String icon,
            @FormParam("description") String description,
            @FormParam("activator") String activator,
            @FormParam("deactivator") String deactivator,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Status name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(icon)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Status icon should be provided."));
        }
        icon = icon.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Status description should be provided."));
        }
        description = description.trim();
        if(StringUtils.isBlank(activator)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Status activator should be provided."));
        }
        activator = activator.trim();
        if(StringUtils.isBlank(deactivator)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Status deactivator should be provided."));
        }
        deactivator = deactivator.trim();
        
        try {
            Status status = new Status();
            status.name = name;
            status.icon = icon;
            status.description = description;
            status.activator = activator;
            status.deactivator = deactivator;
            StatusManager.getInstance().createStatus(status);
            
            Event event = new Event();
            event.type = EventType.CreateStatus;
            event.data.put("data", status);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Status has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create status", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("name") String name, 
            @FormParam("icon") String icon,
            @FormParam("description") String description,
            @FormParam("activator") String activator,
            @FormParam("deactivator") String deactivator,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Status name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(icon)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Status icon should be provided."));
        }
        icon = icon.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Status description should be provided."));
        }
        description = description.trim();
        if(StringUtils.isBlank(activator)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Status activator should be provided."));
        }
        activator = activator.trim();
        if(StringUtils.isBlank(deactivator)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Status deactivator should be provided."));
        }
        deactivator = deactivator.trim();
        
        try {
            Status oldStatus = StatusManager.getInstance().getStatus(id);
            
            Status status = new Status();
            status.id = id;
            status.name = name;
            status.icon = icon;
            status.description = description;
            status.activator = activator;
            status.deactivator = deactivator;
            StatusManager.getInstance().updateStatus(status);
            
            Event event = new Event();
            event.type = EventType.UpdateStatus;
            event.data.put("old_data", oldStatus);
            event.data.put("new_data", status);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Status has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update status", e);
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
            Status status = StatusManager.getInstance().getStatus(id);
            if(status != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Status has been successfully fetched.");
                result.data = status;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Status does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get status", e);
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
            List<Status> statuses = StatusManager.getInstance().getStatuses();
            
            APIResult result = APIResultUtils.buildOKAPIResult("Statuses have been successfully fetched.");
            result.list = statuses;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get statuses", e);
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
            Status status = StatusManager.getInstance().getStatus(id);
            
            StatusManager.getInstance().deleteStatus(id);
            
            Event event = new Event();
            event.type = EventType.DeleteStatus;
            event.data.put("data", status);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Status has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete status", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
