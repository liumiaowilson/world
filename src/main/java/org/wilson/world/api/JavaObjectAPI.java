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
import org.wilson.world.java.JavaObject;
import org.wilson.world.java.JavaObjectInfo;
import org.wilson.world.manager.ExtManager;
import org.wilson.world.manager.JavaObjectManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;

@Path("java_object")
public class JavaObjectAPI {
    private static final Logger logger = Logger.getLogger(JavaObjectAPI.class);
    
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
            List<JavaObjectInfo> javaObjects = JavaObjectManager.getInstance().getJavaObjectInfos();
            Collections.sort(javaObjects, new Comparator<JavaObjectInfo>(){

				@Override
				public int compare(JavaObjectInfo o1, JavaObjectInfo o2) {
					return o1.name.compareTo(o2.name);
				}
            	
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("JavaObjects have been successfully fetched.");
            result.list = javaObjects;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get java objects", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/list_extension")
    @Produces("application/json")
    public Response listExtensions(
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
            List<JavaObject> javaObjects = ExtManager.getInstance().getJavaExtensions();
            Collections.sort(javaObjects, new Comparator<JavaObject>(){

				@Override
				public int compare(JavaObject o1, JavaObject o2) {
					return o1.name.compareTo(o2.name);
				}
            	
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("JavaExtensions have been successfully fetched.");
            result.list = javaObjects;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get java extensions", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
