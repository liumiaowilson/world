package org.wilson.world.api;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
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
import org.wilson.world.manager.MemoryManager;
import org.wilson.world.manager.NotifyManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;

@Path("/memory")
public class MemoryAPI {
    private static final Logger logger = Logger.getLogger(MemoryAPI.class);
    
    @POST
    @Path("/train")
    @Produces("application/json")
    public Response train(
            @FormParam("old") String old,
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
        
        try {
            if(!StringUtils.isBlank(content) && !StringUtils.isBlank(old)) {
                int exp_delta = MemoryManager.getInstance().check(old, content);
                if(exp_delta > 0) {
                    int exp = ExpManager.getInstance().getExp();
                    exp += exp_delta;
                    ExpManager.getInstance().setExp(exp);
                    
                    NotifyManager.getInstance().notifySuccess("Gained an extra experience point from training memory.");
                }
            }
            
            Event event = new Event();
            event.type = EventType.TrainMemory;
            EventManager.getInstance().fireEvent(event);
            
            APIResult result = APIResultUtils.buildOKAPIResult("Memory has been successfully trained.");
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to train memory", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
