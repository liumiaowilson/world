package org.wilson.world.api;

import java.util.HashMap;
import java.util.Map;

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
import org.wilson.world.manager.ScriptManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.manager.TemplateManager;

@Path("template")
public class TemplateAPI {
    private static final Logger logger = Logger.getLogger(TemplateAPI.class);
    
    
    @POST
    @Path("/eval")
    @Produces("application/json")
    public Response eval(
            @FormParam("context") String context,
            @FormParam("template") String template,
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
        
        if(StringUtils.isBlank(context)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Script is needed."));
        }
        context = context.trim();
        
        try {
        	Map<String, Object> vars = new HashMap<String, Object>();
        	Map<String, Object> scriptContext = new HashMap<String, Object>();
        	scriptContext.put("vars", vars);
        	
            ScriptManager.getInstance().run(context, scriptContext);
            
            String ret = TemplateManager.getInstance().evaluate(template, vars);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult(String.valueOf(ret)));
        }
        catch(Exception e) {
            logger.error("failed to evaluate template", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
