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
import org.wilson.world.manager.TerminalManager;

@Path("terminal")
public class TerminalAPI {
    private static final Logger logger = Logger.getLogger(TerminalAPI.class);
    
    @POST
    @Path("/execute")
    @Produces("application/json")
    public Response execute(
            @FormParam("line") String line, 
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
        
        if(StringUtils.isBlank(line)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Line should be provided."));
        }
        line = line.trim();
        
        try {
            String ret = TerminalManager.getInstance().execute(line);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult(ret));
        }
        catch(Exception e) {
            logger.error("failed to execute line", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
