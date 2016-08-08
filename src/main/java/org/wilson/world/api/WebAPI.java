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
import org.wilson.world.manager.SecManager;
import org.wilson.world.manager.WebManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.web.WordInfo;

@Path("/web")
public class WebAPI {
    private static final Logger logger = Logger.getLogger(WebAPI.class);
    
    @POST
    @Path("/lookup")
    @Produces("application/json")
    public Response lookup(
            @FormParam("word") String word,
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
            if(StringUtils.isBlank(word)) {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Word is not provided."));
            }
            word = word.trim();
            word = word.replaceAll(" ", "%20");
            
            WordInfo info = WebManager.getInstance().lookup(word);
            if(info != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Word has been successfully looked up.");
                result.data = info;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Cannot look up the word."));
            }
        }
        catch(Exception e) {
            logger.error("failed to look up word", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
