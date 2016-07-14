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
import org.wilson.world.manager.MonitorManager;
import org.wilson.world.manager.SecManager;

@Path("/monitor")
public class MonitorAPI {
    private static final Logger logger = Logger.getLogger(MonitorAPI.class);
    
    @POST
    @Path("/ack_alert")
    @Produces("application/json")
    public Response ackAlert(
            @FormParam("name") String name, 
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Idea name should be provided."));
        }
        name = name.trim();
        
        try {
            MonitorManager.getInstance().removeAlert(name);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Alert has been successfully acknowledged."));
        }
        catch(Exception e) {
            logger.error("failed to acknowledge alert", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
