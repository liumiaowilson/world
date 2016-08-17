package org.wilson.world.api;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
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
import org.wilson.world.howto.HowToInfo;
import org.wilson.world.manager.HowToManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;

@Path("/how_to")
public class HowToAPI {
    private static final Logger logger = Logger.getLogger(HowToAPI.class);
    
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
            HowToInfo info = HowToManager.getInstance().randomwHowToInfo();
            if(info != null) {
                HowToManager.getInstance().loadHowToInfo(info);
                
                if(info.answer == null) {
                    return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("How-to is not loaded yet."));
                }
                
                APIResult result = APIResultUtils.buildOKAPIResult("Random how-to has been successfully fetched.");
                result.data = info;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Random how-to does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get random novel!", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
