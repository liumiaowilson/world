package org.wilson.world.api;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
import org.wilson.world.java.Jar;
import org.wilson.world.manager.DownloadManager;
import org.wilson.world.manager.JarManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;

@Path("jar")
public class JarAPI {
    private static final Logger logger = Logger.getLogger(JarAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
    		@FormParam("name") String name,
            @FormParam("url") String url, 
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Jar name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(url)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Jar url should be provided."));
        }
        url = url.trim();
        
        try {
        	if(!name.endsWith(".jar")) {
        		name = name + ".jar";
        	}
        	
        	DownloadManager.getInstance().download(url, JarManager.getInstance().getJarDir() + name);
        	
            JarManager.getInstance().addJar(name);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Jar has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create jar", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    
    @GET
    @Path("/get")
    @Produces("application/json")
    public Response get(
            @QueryParam("id") int id,
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
            Jar jar = JarManager.getInstance().getJar(id);
            if(jar != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Jar has been successfully fetched.");
                result.data = jar;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Jar does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get jar", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
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
            List<Jar> jars = JarManager.getInstance().getJars();
            Collections.sort(jars, new Comparator<Jar>(){

				@Override
				public int compare(Jar o1, Jar o2) {
					return Integer.compare(o1.id, o2.id);
				}
            	
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("Jars have been successfully fetched.");
            result.list = jars;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get jars", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/delete")
    @Produces("application/json")
    public Response delete(
            @QueryParam("id") int id,
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
            JarManager.getInstance().deleteJar(id);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Jar has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete jar", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
