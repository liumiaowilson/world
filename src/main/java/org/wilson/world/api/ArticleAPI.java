package org.wilson.world.api;

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
import org.wilson.world.article.ArticleSpeedTrainResult;
import org.wilson.world.event.Event;
import org.wilson.world.event.EventType;
import org.wilson.world.manager.ArticleManager;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.web.ArticleInfo;

@Path("/article")
public class ArticleAPI {
    private static final Logger logger = Logger.getLogger(ArticleAPI.class);
    
    @GET
    @Path("/random")
    @Produces("application/json")
    public Response random(
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
            ArticleInfo info = ArticleManager.getInstance().randomArticleInfo();
            if(info != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Random article has been successfully fetched.");
                result.data = info;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Random article does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get random article!", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Failed to get random article."));
        }
    }
    
    @POST
    @Path("/train_speed")
    @Produces("application/json")
    public Response trainSpeed(
            @FormParam("startTime") long startTime,
            @FormParam("endTime") long endTime,
            @FormParam("title") String title,
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
            ArticleSpeedTrainResult ret = ArticleManager.getInstance().trainSpeed(title, startTime, endTime);
            
            if(ret.errorMessage == null) {
                Event event = new Event();
                event.type = EventType.TrainArticleSpeed;
                EventManager.getInstance().fireEvent(event);
                
                APIResult result = APIResultUtils.buildOKAPIResult("Reading speed of this article is [" + ret.wordsPerMinute + "] words per minute");
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(ret.errorMessage));
            }
        }
        catch(Exception e) {
            logger.error("failed to train article speed!", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Failed to train article speed."));
        }
    }
}
