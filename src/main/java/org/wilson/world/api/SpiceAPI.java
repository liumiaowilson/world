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
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.manager.SpiceManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.Spice;

@Path("spice")
public class SpiceAPI {
    private static final Logger logger = Logger.getLogger(SpiceAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("prerequisite") String prerequisite,
            @FormParam("cost") int cost,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Spice name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(prerequisite)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Spice prerequisite should be provided."));
        }
        prerequisite = prerequisite.trim();
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Spice content should be provided."));
        }
        content = content.trim();
        
        try {
            Spice spice = new Spice();
            spice.name = name;
            spice.prerequisite = prerequisite;
            spice.cost = cost;
            spice.content = content;
            SpiceManager.getInstance().createSpice(spice);
            
            Event event = new Event();
            event.type = EventType.CreateSpice;
            event.data.put("data", spice);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Spice has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create spice", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("name") String name, 
            @FormParam("prerequisite") String prerequisite,
            @FormParam("cost") int cost,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Spice name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(prerequisite)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Spice prerequisite should be provided."));
        }
        prerequisite = prerequisite.trim();
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Spice content should be provided."));
        }
        content = content.trim();
        
        try {
            Spice oldSpice = SpiceManager.getInstance().getSpice(id);
            
            Spice spice = new Spice();
            spice.id = id;
            spice.name = name;
            spice.prerequisite = prerequisite;
            spice.cost = cost;
            spice.content = content;
            SpiceManager.getInstance().updateSpice(spice);
            
            Event event = new Event();
            event.type = EventType.UpdateSpice;
            event.data.put("old_data", oldSpice);
            event.data.put("new_data", spice);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Spice has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update spice", e);
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
            Spice spice = SpiceManager.getInstance().getSpice(id);
            if(spice != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Spice has been successfully fetched.");
                result.data = spice;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Spice does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get spice", e);
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
            List<Spice> spices = SpiceManager.getInstance().getSpices();
            Collections.sort(spices, new Comparator<Spice>(){

				@Override
				public int compare(Spice o1, Spice o2) {
					return Integer.compare(o1.id, o2.id);
				}
            	
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("Spices have been successfully fetched.");
            result.list = spices;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get spices", e);
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
            Spice spice = SpiceManager.getInstance().getSpice(id);
            
            SpiceManager.getInstance().deleteSpice(id);
            
            Event event = new Event();
            event.type = EventType.DeleteSpice;
            event.data.put("data", spice);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Spice has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete spice", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
