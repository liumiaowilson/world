package org.wilson.world.api;

import java.util.List;
import java.util.TimeZone;

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
import org.wilson.world.manager.GoalDefManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.GoalDef;
import org.wilson.world.util.TimeUtils;

@Path("goal_def")
public class GoalDefAPI {
    private static final Logger logger = Logger.getLogger(GoalDefAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("description") String description,
            @FormParam("steps") int steps,
            @FormParam("startTime") String startTimeStr,
            @FormParam("startAmount") int startAmount,
            @FormParam("endTime") String endTimeStr,
            @FormParam("endAmount") int endAmount,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Def name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Def description should be provided."));
        }
        description = description.trim();
        
        try {
            TimeZone tz = (TimeZone)request.getSession().getAttribute("world-timezone");
            if(tz == null) {
                tz = TimeZone.getDefault();
            }
            long startTime = TimeUtils.fromDateString(startTimeStr, tz).getTime();
            long endTime = TimeUtils.fromDateString(endTimeStr, tz).getTime();
            
            GoalDef def = new GoalDef();
            def.name = name;
            def.description = description;
            def.steps = steps;
            def.startTime = startTime;
            def.startAmount = startAmount;
            def.endTime = endTime;
            def.endAmount = endAmount;
            GoalDefManager.getInstance().createGoalDef(def);
            
            Event event = new Event();
            event.type = EventType.CreateGoalDef;
            event.data.put("data", def);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Def has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create def", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("name") String name, 
            @FormParam("description") String description,
            @FormParam("steps") int steps,
            @FormParam("startTime") String startTimeStr,
            @FormParam("startAmount") int startAmount,
            @FormParam("endTime") String endTimeStr,
            @FormParam("endAmount") int endAmount,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Def name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Def description should be provided."));
        }
        description = description.trim();
        
        try {
            TimeZone tz = (TimeZone)request.getSession().getAttribute("world-timezone");
            if(tz == null) {
                tz = TimeZone.getDefault();
            }
            long startTime = TimeUtils.fromDateString(startTimeStr, tz).getTime();
            long endTime = TimeUtils.fromDateString(endTimeStr, tz).getTime();
            
            GoalDef oldDef = GoalDefManager.getInstance().getGoalDef(id);
            
            GoalDef def = new GoalDef();
            def.id = id;
            def.name = name;
            def.description = description;
            def.steps = steps;
            def.startTime = startTime;
            def.startAmount = startAmount;
            def.endTime = endTime;
            def.endAmount = endAmount;
            GoalDefManager.getInstance().updateGoalDef(def);
            
            Event event = new Event();
            event.type = EventType.UpdateGoalDef;
            event.data.put("old_data", oldDef);
            event.data.put("new_data", def);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Def has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update def", e);
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
            GoalDef def = GoalDefManager.getInstance().getGoalDef(id);
            if(def != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Def has been successfully fetched.");
                result.data = def;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Def does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get def", e);
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
            List<GoalDef> defs = GoalDefManager.getInstance().getGoalDefs();
            
            APIResult result = APIResultUtils.buildOKAPIResult("Defs have been successfully fetched.");
            result.list = defs;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get defs", e);
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
            GoalDef def = GoalDefManager.getInstance().getGoalDef(id);
            
            GoalDefManager.getInstance().deleteGoalDef(id);
            
            Event event = new Event();
            event.type = EventType.DeleteGoalDef;
            event.data.put("data", def);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Def has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete def", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/finish")
    @Produces("application/json")
    public Response finish(
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
            GoalDef def = GoalDefManager.getInstance().getGoalDef(id);
            if(def != null) {
                boolean completed = GoalDefManager.getInstance().finish(def);
                
                if(completed) {
                    Event event = new Event();
                    event.type = EventType.CompleteGoalDef;
                    event.data.put("data", def);
                    EventManager.getInstance().fireEvent(event);
                }
                
                APIResult result = APIResultUtils.buildOKAPIResult("Def has been successfully finished.");
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Def does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to finish def", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
