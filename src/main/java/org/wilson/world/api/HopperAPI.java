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
import org.wilson.world.manager.HopperManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.manager.WebManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.Hopper;
import org.wilson.world.web.WebJob;

@Path("hopper")
public class HopperAPI {
    private static final Logger logger = Logger.getLogger(HopperAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("description") String description,
            @FormParam("period") int period,
            @FormParam("action") String action,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Hopper name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Hopper description should be provided."));
        }
        description = description.trim();
        if(StringUtils.isBlank(action)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Hopper action should be provided."));
        }
        action = action.trim();
        
        try {
            Hopper hopper = new Hopper();
            hopper.name = name;
            hopper.description = description;
            hopper.period = period;
            hopper.action = action;
            HopperManager.getInstance().createHopper(hopper);
            
            Event event = new Event();
            event.type = EventType.CreateHopper;
            event.data.put("data", hopper);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Hopper has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create hopper", e);
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
            @FormParam("period") int period,
            @FormParam("action") String action,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Hopper name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Hopper description should be provided."));
        }
        description = description.trim();
        if(StringUtils.isBlank(action)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Hopper action should be provided."));
        }
        action = action.trim();
        
        try {
            Hopper oldHopper = HopperManager.getInstance().getHopper(id);
            
            Hopper hopper = new Hopper();
            hopper.id = id;
            hopper.name = name;
            hopper.description = description;
            hopper.period = period;
            hopper.action = action;
            HopperManager.getInstance().updateHopper(hopper);
            
            Event event = new Event();
            event.type = EventType.UpdateHopper;
            event.data.put("old_data", oldHopper);
            event.data.put("new_data", hopper);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Hopper has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update hopper", e);
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
            Hopper hopper = HopperManager.getInstance().getHopper(id);
            if(hopper != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Hopper has been successfully fetched.");
                result.data = hopper;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Hopper does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get hopper", e);
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
            List<Hopper> hoppers = HopperManager.getInstance().getHoppers();
            
            APIResult result = APIResultUtils.buildOKAPIResult("Hoppers have been successfully fetched.");
            result.list = hoppers;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get hoppers", e);
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
            Hopper hopper = HopperManager.getInstance().getHopper(id);
            
            HopperManager.getInstance().deleteHopper(id);
            
            Event event = new Event();
            event.type = EventType.DeleteHopper;
            event.data.put("data", hopper);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Hopper has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete hopper", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/enable")
    @Produces("application/json")
    public Response enable(
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
            WebJob job = WebManager.getInstance().getWebJob(id);
            if(job != null) {
                WebManager.getInstance().enableJob(job);

                return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Web job has been successfully enabled."));
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Web job does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to enable web job", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/disable")
    @Produces("application/json")
    public Response disable(
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
            WebJob job = WebManager.getInstance().getWebJob(id);
            if(job != null) {
                WebManager.getInstance().disableJob(job);

                return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Web job has been successfully disabled."));
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Web job does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to disable web job", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/debug")
    @Produces("application/json")
    public Response debug(
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
            WebJob job = WebManager.getInstance().getWebJob(id);
            if(job != null) {
                WebManager.getInstance().debug(job);

                return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Web job has been successfully debugged."));
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Web job does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to debug web job", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
