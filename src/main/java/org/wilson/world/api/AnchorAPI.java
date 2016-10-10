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
import org.wilson.world.manager.AnchorManager;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.Anchor;

@Path("anchor")
public class AnchorAPI {
    private static final Logger logger = Logger.getLogger(AnchorAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("type") String type,
            @FormParam("stimuli") String stimuli,
            @FormParam("response") String response,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Anchor name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(type)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Anchor type should be provided."));
        }
        type = type.trim();
        if(StringUtils.isBlank(stimuli)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Anchor stimuli should be provided."));
        }
        stimuli = stimuli.trim();
        if(StringUtils.isBlank(response)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Anchor response should be provided."));
        }
        response = response.trim();
        
        try {
            Anchor anchor = new Anchor();
            anchor.name = name;
            anchor.type = type;
            anchor.stimuli = stimuli;
            anchor.response = response;
            AnchorManager.getInstance().createAnchor(anchor);
            
            Event event = new Event();
            event.type = EventType.CreateAnchor;
            event.data.put("data", anchor);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Anchor has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create anchor", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("name") String name, 
            @FormParam("type") String type,
            @FormParam("stimuli") String stimuli,
            @FormParam("response") String response,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Anchor name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(type)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Anchor type should be provided."));
        }
        type = type.trim();
        if(StringUtils.isBlank(stimuli)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Anchor stimuli should be provided."));
        }
        stimuli = stimuli.trim();
        if(StringUtils.isBlank(response)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Anchor response should be provided."));
        }
        response = response.trim();
        
        try {
            Anchor oldAnchor = AnchorManager.getInstance().getAnchor(id);
            
            Anchor anchor = new Anchor();
            anchor.id = id;
            anchor.name = name;
            anchor.type = type;
            anchor.stimuli = stimuli;
            anchor.response = response;
            AnchorManager.getInstance().updateAnchor(anchor);
            
            Event event = new Event();
            event.type = EventType.UpdateAnchor;
            event.data.put("old_data", oldAnchor);
            event.data.put("new_data", anchor);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Anchor has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update anchor", e);
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
            Anchor anchor = AnchorManager.getInstance().getAnchor(id);
            if(anchor != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Anchor has been successfully fetched.");
                result.data = anchor;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Anchor does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get anchor", e);
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
            List<Anchor> anchors = AnchorManager.getInstance().getAnchors();
            
            APIResult result = APIResultUtils.buildOKAPIResult("Anchors have been successfully fetched.");
            result.list = anchors;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get anchors", e);
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
            Anchor anchor = AnchorManager.getInstance().getAnchor(id);
            
            AnchorManager.getInstance().deleteAnchor(id);
            
            Event event = new Event();
            event.type = EventType.DeleteAnchor;
            event.data.put("data", anchor);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Anchor has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete anchor", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
