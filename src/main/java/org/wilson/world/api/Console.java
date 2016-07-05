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
import org.wilson.world.manager.ConsoleManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.QueryResult;

@Path("console")
public class Console {
    private static final Logger logger = Logger.getLogger(Console.class);
    
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
    
    @POST
    @Path("/execute")
    @Produces("application/json")
    public Response execute(
            @FormParam("sql") String sql,
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
        
        if(StringUtils.isBlank(sql)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("SQL is needed."));
        }
        
        try {
            QueryResult result = ConsoleManager.getInstance().execute(sql);
            APIResult ret = APIResultUtils.buildOKAPIResult("SQL executed successfully.");
            ret.data = result;
            return APIResultUtils.buildJSONResponse(ret);
        }
        catch(Exception e) {
            logger.error("failed to execute sql!", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Failed to execute sql."));
        }
    }
    
    @GET
    @Path("/delete_logs")
    @Produces("application/json")
    public Response deleteLogs(
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
            String result = ConsoleManager.getInstance().deleteLogs();
            APIResult ret = APIResultUtils.buildOKAPIResult(result);
            return APIResultUtils.buildJSONResponse(ret);
        }
        catch(Exception e) {
            logger.error("failed to delete logs!", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Failed to delete logs."));
        }
    }
    
    @GET
    @Path("/release_memory")
    @Produces("application/json")
    public Response releaseMemory(
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
            ConsoleManager.getInstance().releaseMemory();
            APIResult ret = APIResultUtils.buildOKAPIResult("Memory released successfully.");
            return APIResultUtils.buildJSONResponse(ret);
        }
        catch(Exception e) {
            logger.error("failed to release memory!", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Failed to release memory."));
        }
    }
}
