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
import org.wilson.world.java.JavaSuggestion;
import org.wilson.world.java.JavaTemplate;
import org.wilson.world.manager.JavaTemplateManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;

@Path("java_template")
public class JavaTemplateAPI {
    private static final Logger logger = Logger.getLogger(JavaTemplateAPI.class);
    
    @POST
    @Path("/get_template")
    @Produces("application/json")
    public Response getTemplate(
    		@FormParam("name") String name,
    		@FormParam("templateName") String templateName,
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
        	return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Name is needed."));
        }
        name = name.trim();
        if(StringUtils.isBlank(templateName)) {
        	return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Template name is needed."));
        }
        templateName = templateName.trim();
        
        try {
            JavaTemplate template = JavaTemplateManager.getInstance().getJavaTemplate(templateName);
            if(template != null) {
            	String className = JavaTemplateManager.getInstance().toClassName(name);
            	template.className = className;
            	JavaSuggestion suggestion = JavaTemplateManager.getInstance().generateCode(template);

                APIResult result = APIResultUtils.buildOKAPIResult("JavaTemplate have been successfully fetched.");
                result.data = suggestion;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
            	return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("No such java template could be found."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get java template", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
