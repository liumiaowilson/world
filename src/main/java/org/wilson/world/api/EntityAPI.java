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
import org.wilson.world.entity.EntityDefinition;
import org.wilson.world.entity.EntityDelegator;
import org.wilson.world.event.Event;
import org.wilson.world.event.EventType;
import org.wilson.world.manager.EntityManager;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.Entity;

import net.sf.json.JSONObject;

@Path("entity")
public class EntityAPI {
    private static final Logger logger = Logger.getLogger(EntityAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
    		@FormParam("type") String type,
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
        
        if(StringUtils.isBlank(type)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Entity type should be provided."));
        }
        type = type.trim();
        if(StringUtils.isBlank(name)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(type + " name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(type + " content should be provided."));
        }
        content = content.trim();
        
        try {
        	EntityDefinition def = EntityManager.getInstance().getEntityDefinition(type);
        	if(def == null) {
        		return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Failed to find such entity definition."));
        	}
        	
        	JSONObject obj = JSONObject.fromObject(content);
        	Entity entity = def.toEntity(obj);
        	if(entity == null) {
        		return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Failed to build entity."));
        	}
            entity.name = name;
            entity.type = type;
            EntityManager.getInstance().create(entity);
            
            Event event = new Event();
            event.type = EventType.CreateEntity;
            event.data.put("data", entity);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult(type + " has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create " + type, e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("type") String type,
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
        
        if(StringUtils.isBlank(type)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Entity type should be provided."));
        }
        type = type.trim();
        if(StringUtils.isBlank(name)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(type + " name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(type + " content should be provided."));
        }
        content = content.trim();
        
        try {
            Entity oldEntity = EntityManager.getInstance().getEntity(type, id);
            
            EntityDefinition def = EntityManager.getInstance().getEntityDefinition(type);
        	if(def == null) {
        		return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Failed to find such entity definition."));
        	}
        	
        	JSONObject obj = JSONObject.fromObject(content);
        	Entity entity = def.toEntity(obj);
        	if(entity == null) {
        		return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Failed to build entity."));
        	}
            entity.name = name;
            entity.type = type;
            EntityManager.getInstance().update(entity);
            
            Event event = new Event();
            event.type = EventType.UpdateEntity;
            event.data.put("old_data", oldEntity);
            event.data.put("new_data", entity);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult(type + " has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update " + type, e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/get")
    @Produces("application/json")
    public Response get(
            @QueryParam("id") int id,
            @QueryParam("type") String type,
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
        
        if(StringUtils.isBlank(type)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Entity type should be provided."));
        }
        type = type.trim();
        
        try {
            Entity entity = EntityManager.getInstance().getEntity(type, id, true);
            if(entity != null) {
                APIResult result = APIResultUtils.buildOKAPIResult(type + " has been successfully fetched.");
                result.data = entity;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(type + " does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get " + type, e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/list")
    @Produces("application/json")
    public Response list(
    		@QueryParam("type") String type,
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
        
        if(StringUtils.isBlank(type)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Entity type should be provided."));
        }
        type = type.trim();
        
        try {
            List<Entity> entities = EntityManager.getInstance().getEntities(type);
            Collections.sort(entities, new Comparator<Entity>() {

				@Override
				public int compare(Entity o1, Entity o2) {
					return Integer.compare(o1.id, o2.id);
				}
            	
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult(type + "(s) have been successfully fetched.");
            result.list = entities;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get " + type + "(s)", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/delete")
    @Produces("application/json")
    public Response delete(
            @QueryParam("id") int id,
            @QueryParam("type") String type,
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
        
        if(StringUtils.isBlank(type)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Entity type should be provided."));
        }
        type = type.trim();
        
        try {
            Entity entity = EntityManager.getInstance().getEntity(type, id);
            
            EntityManager.getInstance().delete(entity);
            
            Event event = new Event();
            event.type = EventType.DeleteEntity;
            event.data.put("data", entity);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult(type + " has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete " + type, e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/reload")
    @Produces("application/json")
    public Response reload(
    		@QueryParam("type") String type,
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
        
        if(StringUtils.isBlank(type)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Entity type should be provided."));
        }
        type = type.trim();
        
        try {
            EntityDelegator delegator = EntityManager.getInstance().getEntityDelegator(type);
            if(delegator != null) {
            	delegator.load();
            	
                APIResult result = APIResultUtils.buildOKAPIResult(type + "has been successfully loaded.");
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
            	return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Entity delegator not found for [" + type + "]."));
            }
        }
        catch(Exception e) {
            logger.error("failed to load " + type + "", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
