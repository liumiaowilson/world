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
import org.wilson.world.manager.TaskAttrRuleManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.TaskAttrRule;

@Path("task_attr_rule")
public class TaskAttrRuleAPI {
    private static final Logger logger = Logger.getLogger(TaskAttrRuleAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name,
            @FormParam("priority") String priority,
            @FormParam("policy") String policy,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Task attr rule name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(policy)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Task attr rule policy should be provided."));
        }
        policy = policy.trim();
        
        try {
            TaskAttrRule rule = new TaskAttrRule();
            rule.name = name;
            try {
                rule.priority = Integer.parseInt(priority);
            }
            catch(Exception e) {
                rule.priority = 0;
            }
            rule.policy = policy;
            rule.impl = impl;
            TaskAttrRuleManager.getInstance().createTaskAttrRule(rule);
            
            Event event = new Event();
            event.type = EventType.CreateTaskAttrRule;
            event.data.put("data", rule);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Task attr rule has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create task attr rule", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("name") String name, 
            @FormParam("priority") String priority,
            @FormParam("policy") String policy,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Task attr rule name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(policy)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Task attr rule policy should be provided."));
        }
        policy = policy.trim();
        
        try {
            TaskAttrRule oldRule = TaskAttrRuleManager.getInstance().getTaskAttrRule(id);
            
            TaskAttrRule rule = new TaskAttrRule();
            rule.id = id;
            rule.name = name;
            try {
                rule.priority = Integer.parseInt(priority);
            }
            catch(Exception e) {
                rule.priority = 0;
            }
            rule.policy = policy;
            rule.impl = impl;
            TaskAttrRuleManager.getInstance().updateTaskAttrRule(rule);
            
            Event event = new Event();
            event.type = EventType.UpdateTaskAttrRule;
            event.data.put("old_data", oldRule);
            event.data.put("new_data", rule);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Task attr rule has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update task attr rule", e);
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
            TaskAttrRule rule = TaskAttrRuleManager.getInstance().getTaskAttrRule(id);
            if(rule != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Task attr rule has been successfully fetched.");
                result.data = rule;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Task attr rule does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get task attr rule", e);
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
            List<TaskAttrRule> rules = TaskAttrRuleManager.getInstance().getTaskAttrRules();
            
            APIResult result = APIResultUtils.buildOKAPIResult("Task attr rules have been successfully fetched.");
            result.list = rules;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get task attr rules", e);
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
            TaskAttrRule rule = TaskAttrRuleManager.getInstance().getTaskAttrRule(id);
            
            TaskAttrRuleManager.getInstance().deleteTaskAttrRule(id);
            
            Event event = new Event();
            event.type = EventType.DeleteTaskAttrRule;
            event.data.put("data", rule);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Task attr rule has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete task attr rule", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
