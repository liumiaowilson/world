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
import org.wilson.world.manager.ScheduleManager;
import org.wilson.world.manager.SecManager;

@Path("/job")
public class JobAPI {
    private static final Logger logger = Logger.getLogger(JobAPI.class);
    
    @GET
    @Path("/run_job")
    @Produces("application/json")
    public Response runJob(
            @QueryParam("name") String name,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Job name is needed."));
        }
        name = name.trim();
        
        try {
            ScheduleManager.getInstance().run(name);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Job run successfully."));
        }
        catch(Exception e) {
            logger.error("failed to run job", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
