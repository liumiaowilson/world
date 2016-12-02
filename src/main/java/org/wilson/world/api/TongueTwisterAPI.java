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
import org.wilson.world.manager.TongueTwisterManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.TongueTwister;

@Path("tongue_twister")
public class TongueTwisterAPI {
    private static final Logger logger = Logger.getLogger(TongueTwisterAPI.class);
    
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("TongueTwister name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("TongueTwister content should be provided."));
        }
        content = content.trim();
        
        try {
            TongueTwister tt = new TongueTwister();
            tt.name = name;
            tt.content = content;
            TongueTwisterManager.getInstance().createTongueTwister(tt);
            
            Event event = new Event();
            event.type = EventType.CreateTongueTwister;
            event.data.put("data", tt);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("TongueTwister has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create tongue twister", e);
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("TongueTwister name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("TongueTwister content should be provided."));
        }
        content = content.trim();
        
        try {
            TongueTwister oldTT = TongueTwisterManager.getInstance().getTongueTwister(id);
            
            TongueTwister tt = new TongueTwister();
            tt.id = id;
            tt.name = name;
            tt.content = content;
            TongueTwisterManager.getInstance().updateTongueTwister(tt);
            
            Event event = new Event();
            event.type = EventType.UpdateTongueTwister;
            event.data.put("old_data", oldTT);
            event.data.put("new_data", tt);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("TongueTwister has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update tongue twister", e);
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
            TongueTwister tt = TongueTwisterManager.getInstance().getTongueTwister(id);
            if(tt != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("TongueTwister has been successfully fetched.");
                result.data = tt;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("TongueTwister does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get tongue twister", e);
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
            List<TongueTwister> tts = TongueTwisterManager.getInstance().getTongueTwisters();
            Collections.sort(tts, new Comparator<TongueTwister>(){

				@Override
				public int compare(TongueTwister o1, TongueTwister o2) {
					return Integer.compare(o1.id, o2.id);
				}
            	
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("TongueTwisters have been successfully fetched.");
            result.list = tts;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get tongue twisters", e);
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
            TongueTwister tt = TongueTwisterManager.getInstance().getTongueTwister(id);
            
            TongueTwisterManager.getInstance().deleteTongueTwister(id);
            
            Event event = new Event();
            event.type = EventType.DeleteTongueTwister;
            event.data.put("data", tt);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("TongueTwister has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete tongue twister", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
