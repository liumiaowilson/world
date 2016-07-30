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
import org.wilson.world.manager.RomanceFactorManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.RomanceFactor;

@Path("romance_factor")
public class RomanceFactorAPI {
    private static final Logger logger = Logger.getLogger(RomanceFactorAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
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
        
        if(StringUtils.isBlank(name)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Romance factor name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Romance factor content should be provided."));
        }
        content = content.trim();
        
        try {
            RomanceFactor factor = new RomanceFactor();
            factor.name = name;
            factor.content = content;
            RomanceFactorManager.getInstance().createRomanceFactor(factor);
            
            Event event = new Event();
            event.type = EventType.CreateRomanceFactor;
            event.data.put("data", factor);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Romance factor has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create factor", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
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
        
        if(StringUtils.isBlank(name)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Romance factor name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Romance factor content should be provided."));
        }
        content = content.trim();
        
        try {
            RomanceFactor oldFactor = RomanceFactorManager.getInstance().getRomanceFactor(id);
            
            RomanceFactor factor = new RomanceFactor();
            factor.id = id;
            factor.name = name;
            factor.content = content;
            RomanceFactorManager.getInstance().updateRomanceFactor(factor);
            
            Event event = new Event();
            event.type = EventType.UpdateRomanceFactor;
            event.data.put("old_data", oldFactor);
            event.data.put("new_data", factor);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Romance factor has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update factor", e);
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
            RomanceFactor factor = RomanceFactorManager.getInstance().getRomanceFactor(id);
            if(factor != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Romance factor has been successfully fetched.");
                result.data = factor;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Romance factor does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get factor", e);
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
            List<RomanceFactor> factors = RomanceFactorManager.getInstance().getRomanceFactors();
            
            APIResult result = APIResultUtils.buildOKAPIResult("Romance factors have been successfully fetched.");
            result.list = factors;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get factors", e);
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
            RomanceFactor factor = RomanceFactorManager.getInstance().getRomanceFactor(id);
            
            RomanceFactorManager.getInstance().deleteRomanceFactor(id);
            
            Event event = new Event();
            event.type = EventType.DeleteRomanceFactor;
            event.data.put("data", factor);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Romance factor has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete factor", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
