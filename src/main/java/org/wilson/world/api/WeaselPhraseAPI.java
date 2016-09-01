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
import org.wilson.world.manager.ExpManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.manager.WeaselPhraseManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.WeaselPhrase;

@Path("weasel_phrase")
public class WeaselPhraseAPI {
    private static final Logger logger = Logger.getLogger(WeaselPhraseAPI.class);
    
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
            WeaselPhrase phrase = WeaselPhraseManager.getInstance().getWeaselPhrase(id);
            if(phrase != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("WeaselPhrase has been successfully fetched.");
                result.data = phrase;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("WeaselPhrase does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get phrase", e);
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
            List<WeaselPhrase> phrases = WeaselPhraseManager.getInstance().getWeaselPhrases();
            
            APIResult result = APIResultUtils.buildOKAPIResult("WeaselPhrases have been successfully fetched.");
            result.list = phrases;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get phrases", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/train")
    @Produces("application/json")
    public Response train(
            @FormParam("examples") String examples,
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
            if(!StringUtils.isBlank(examples)) {
                ExpManager.getInstance().train(examples, "Gained an extra experience point from training weasel phrases.");
            }
            
            Event event = new Event();
            event.type = EventType.TrainWeaselPhrase;
            EventManager.getInstance().fireEvent(event);
            
            APIResult result = APIResultUtils.buildOKAPIResult("Weasel phrase has been successfully trained.");
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to train weasel phrase", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
