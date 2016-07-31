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
import org.wilson.world.manager.SkillDataManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.SkillData;

@Path("skill_data")
public class SkillDataAPI {
    private static final Logger logger = Logger.getLogger(SkillDataAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("description") String description,
            @FormParam("type") String type,
            @FormParam("scope") String scope,
            @FormParam("target") String target,
            @FormParam("cost") int cost,
            @FormParam("cooldown") int cooldown,
            @FormParam("canTrigger") String canTrigger,
            @FormParam("trigger") String trigger,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Data name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Data description should be provided."));
        }
        description = description.trim();
        if(StringUtils.isBlank(type)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Data type should be provided."));
        }
        type = type.trim();
        if(StringUtils.isBlank(scope)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Data scope should be provided."));
        }
        scope = scope.trim();
        if(StringUtils.isBlank(target)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Data target should be provided."));
        }
        target = target.trim();
        if(StringUtils.isBlank(canTrigger)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Data canTrigger should be provided."));
        }
        canTrigger = canTrigger.trim();
        if(StringUtils.isBlank(trigger)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Data trigger should be provided."));
        }
        trigger = trigger.trim();
        
        try {
            SkillData data = new SkillData();
            data.name = name;
            data.description = description;
            data.type = type;
            data.scope = scope;
            data.target = target;
            data.cost = cost;
            data.cooldown = cooldown;
            data.canTrigger = canTrigger;
            data.trigger = trigger;
            SkillDataManager.getInstance().createSkillData(data);
            
            Event event = new Event();
            event.type = EventType.CreateSkillData;
            event.data.put("data", data);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Data has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create data", e);
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
            @FormParam("type") String type,
            @FormParam("scope") String scope,
            @FormParam("target") String target,
            @FormParam("cost") int cost,
            @FormParam("cooldown") int cooldown,
            @FormParam("canTrigger") String canTrigger,
            @FormParam("trigger") String trigger,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Data name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Data description should be provided."));
        }
        description = description.trim();
        if(StringUtils.isBlank(type)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Data type should be provided."));
        }
        type = type.trim();
        if(StringUtils.isBlank(scope)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Data scope should be provided."));
        }
        scope = scope.trim();
        if(StringUtils.isBlank(target)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Data target should be provided."));
        }
        target = target.trim();
        if(StringUtils.isBlank(canTrigger)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Data canTrigger should be provided."));
        }
        canTrigger = canTrigger.trim();
        if(StringUtils.isBlank(trigger)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Data trigger should be provided."));
        }
        trigger = trigger.trim();
        
        try {
            SkillData oldData = SkillDataManager.getInstance().getSkillData(id);
            
            SkillData data = new SkillData();
            data.id = id;
            data.name = name;
            data.description = description;
            data.type = type;
            data.scope = scope;
            data.target = target;
            data.cost = cost;
            data.cooldown = cooldown;
            data.canTrigger = canTrigger;
            data.trigger = trigger;
            SkillDataManager.getInstance().updateSkillData(data);
            
            Event event = new Event();
            event.type = EventType.UpdateSkillData;
            event.data.put("old_data", oldData);
            event.data.put("new_data", data);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Data has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update data", e);
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
            SkillData data = SkillDataManager.getInstance().getSkillData(id);
            if(data != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Data has been successfully fetched.");
                result.data = data;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Data does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get data", e);
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
            List<SkillData> datas = SkillDataManager.getInstance().getSkillDatas();
            APIResult result = APIResultUtils.buildOKAPIResult("Datas have been successfully fetched.");
            result.list = datas;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get datas", e);
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
            SkillData data = SkillDataManager.getInstance().getSkillData(id);
            
            SkillDataManager.getInstance().deleteSkillData(id);
            
            Event event = new Event();
            event.type = EventType.DeleteSkillData;
            event.data.put("data", data);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Data has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete data", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
