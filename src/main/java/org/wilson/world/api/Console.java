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
import org.wilson.world.api.util.APIResultUtils;
import org.wilson.world.manager.ConsoleManager;
import org.wilson.world.manager.SecManager;

@Path("console")
public class Console {
    @POST
    @Path("/run")
    @Produces("application/json")
    public Response listTableNames(
            @FormParam("cmd") String cmd,
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
        
        if(StringUtils.isBlank(cmd)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Command is needed."));
        }
        
        String output = ConsoleManager.getInstance().run(cmd);
        
        return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult(output));
    }
}
