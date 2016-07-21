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
import org.wilson.world.manager.TaskSeedManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.TaskSeed;

@Path("task_seed")
public class TaskSeedAPI {
    private static final Logger logger = Logger.getLogger(TaskSeedAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("pattern") String pattern,
            @FormParam("spawner") String spawner,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Task seed name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(pattern)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Task seed pattern should be provided."));
        }
        pattern = pattern.trim();
        if(StringUtils.isBlank(spawner)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Task seed spawner should be provided."));
        }
        spawner = spawner.trim();
        
        try {
            TaskSeed seed = new TaskSeed();
            seed.name = name;
            seed.pattern = pattern;
            seed.spawner = spawner;
            TaskSeedManager.getInstance().createTaskSeed(seed);
            
            Event event = new Event();
            event.type = EventType.CreateTaskSeed;
            event.data.put("data", seed);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Task seed has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create task seed", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("name") String name, 
            @FormParam("pattern") String pattern,
            @FormParam("spawner") String spawner,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Task seed name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(pattern)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Task seed pattern should be provided."));
        }
        pattern = pattern.trim();
        if(StringUtils.isBlank(spawner)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Task seed spawner should be provided."));
        }
        spawner = spawner.trim();
        
        try {
            TaskSeed oldSeed = TaskSeedManager.getInstance().getTaskSeed(id);
            
            TaskSeed seed = new TaskSeed();
            seed.id = id;
            seed.name = name;
            seed.pattern = pattern;
            seed.spawner = spawner;
            TaskSeedManager.getInstance().updateTaskSeed(seed);
            
            Event event = new Event();
            event.type = EventType.UpdateTaskSeed;
            event.data.put("old_data", oldSeed);
            event.data.put("new_data", seed);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Task seed has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update task seed", e);
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
            TaskSeed seed = TaskSeedManager.getInstance().getTaskSeed(id);
            if(seed != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Task seed has been successfully fetched.");
                result.data = seed;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Task seed does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get task seed", e);
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
            List<TaskSeed> seeds = TaskSeedManager.getInstance().getTaskSeeds();
            
            APIResult result = APIResultUtils.buildOKAPIResult("Task seeds have been successfully fetched.");
            result.list = seeds;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get task seeds", e);
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
            TaskSeed seed = TaskSeedManager.getInstance().getTaskSeed(id);
            
            TaskSeedManager.getInstance().deleteTaskSeed(id);
            
            Event event = new Event();
            event.type = EventType.DeleteTaskSeed;
            event.data.put("data", seed);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Task seed has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete task seed", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
