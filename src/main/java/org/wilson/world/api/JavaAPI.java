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
import org.wilson.world.java.RunJavaInfo;
import org.wilson.world.manager.JavaManager;
import org.wilson.world.manager.SecManager;

@Path("/java")
public class JavaAPI {
    private static final Logger logger = Logger.getLogger(JavaAPI.class);
    
    @POST
    @Path("/run")
    @Produces("application/json")
    public Response run(
            @FormParam("source") String source,
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
        
        if(StringUtils.isBlank(source)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Source is needed."));
        }
        source = source.trim();
        
        try {
            RunJavaInfo info = JavaManager.getInstance().run(source);
            String ret = null;
            if(info.isSuccessful) {
                ret = info.log;
            }
            else {
                ret = info.message;
                if(info.lineNumber != 0) {
                    ret += " at line " + info.lineNumber;
                }
            }
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult(ret));
        }
        catch(Exception e) {
            logger.error("failed to run source", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
