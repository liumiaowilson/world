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
import org.wilson.world.manager.TaskFollowerManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.TaskFollower;

@Path("task_follower")
public class TaskFollowerAPI {
    private static final Logger logger = Logger.getLogger(TaskFollowerAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("symbol") String symbol,
            @FormParam("impl") String impl,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Task follower name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(symbol)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Task follower symbol should be provided."));
        }
        symbol = symbol.trim();
        if(StringUtils.isBlank(impl)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Task follower implementation should be provided."));
        }
        impl = impl.trim();
        
        try {
            TaskFollower follower = new TaskFollower();
            follower.name = name;
            follower.symbol = symbol;
            follower.impl = impl;
            TaskFollowerManager.getInstance().createTaskFollower(follower);
            
            Event event = new Event();
            event.type = EventType.CreateTaskFollower;
            event.data.put("data", follower);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Task follower has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create task follower", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("name") String name, 
            @FormParam("symbol") String symbol,
            @FormParam("impl") String impl,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Task follower name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(symbol)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Task follower symbol should be provided."));
        }
        symbol = symbol.trim();
        if(StringUtils.isBlank(impl)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Task follower implementation should be provided."));
        }
        impl = impl.trim();
        
        try {
            TaskFollower oldFollower = TaskFollowerManager.getInstance().getTaskFollower(id);
            
            TaskFollower follower = new TaskFollower();
            follower.id = id;
            follower.name = name;
            follower.symbol = symbol;
            follower.impl = impl;
            TaskFollowerManager.getInstance().updateTaskFollower(follower);
            
            Event event = new Event();
            event.type = EventType.UpdateTaskFollower;
            event.data.put("old_data", oldFollower);
            event.data.put("new_data", follower);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Task follower has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update task follower", e);
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
            TaskFollower follower = TaskFollowerManager.getInstance().getTaskFollower(id);
            if(follower != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Task follower has been successfully fetched.");
                result.data = follower;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Task follower does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get task follower", e);
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
            List<TaskFollower> followers = TaskFollowerManager.getInstance().getTaskFollowers();
            
            APIResult result = APIResultUtils.buildOKAPIResult("Task followers have been successfully fetched.");
            result.list = followers;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get task followers", e);
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
            TaskFollower follower = TaskFollowerManager.getInstance().getTaskFollower(id);
            
            TaskFollowerManager.getInstance().deleteTaskFollower(id);
            
            Event event = new Event();
            event.type = EventType.DeleteTaskFollower;
            event.data.put("data", follower);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Task follower has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete task follower", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
