package org.wilson.world.api;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
import org.wilson.world.java.JavaClass;
import org.wilson.world.java.JavaExtensionPoint;
import org.wilson.world.manager.ExtManager;
import org.wilson.world.manager.JavaClassManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;

@Path("java_class")
public class JavaClassAPI {
    private static final Logger logger = Logger.getLogger(JavaClassAPI.class);
    
    @GET
    @Path("/list")
    @Produces("application/json")
    public Response list(
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
            List<JavaClass> javaClasses = JavaClassManager.getInstance().getJavaClasses();
            Collections.sort(javaClasses, new Comparator<JavaClass>(){

				@Override
				public int compare(JavaClass o1, JavaClass o2) {
					return o1.name.compareTo(o2.name);
				}
            	
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("JavaClasses have been successfully fetched.");
            result.list = javaClasses;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get java classes", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/list_extension_point")
    @Produces("application/json")
    public Response listExtensionPoints(
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
            List<JavaExtensionPoint> eps = ExtManager.getInstance().getJavaExtensionPoints();
            Collections.sort(eps, new Comparator<JavaExtensionPoint>(){

				@Override
				public int compare(JavaExtensionPoint o1, JavaExtensionPoint o2) {
					return o1.name.compareTo(o2.name);
				}
            	
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("JavaExtensionPoints have been successfully fetched.");
            result.list = eps;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get java extension points", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
