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
import org.wilson.world.manager.CharManager;
import org.wilson.world.manager.SecManager;

@Path("char")
public class CharAPI {
    private static final Logger logger = Logger.getLogger(CharAPI.class);
    
    @POST
    @Path("/exchange")
    @Produces("application/json")
    public Response exchange(
            @FormParam("coins") int coins, 
            @FormParam("points") int points,
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
            CharManager.getInstance().setCoins(coins);
            CharManager.getInstance().setSkillPoints(points);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Coins and skill points have been successfully exchanged."));
        }
        catch(Exception e) {
            logger.error("failed to exchange coins and skill points", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
