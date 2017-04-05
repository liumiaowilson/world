package org.wilson.world.api;

import java.io.ByteArrayInputStream;
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
import org.wilson.world.file.LocalFile;
import org.wilson.world.manager.LocalFileManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;

@Path("local_file")
public class LocalFileAPI {
    private static final Logger logger = Logger.getLogger(LocalFileAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("content") String content,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("LocalFile name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("LocalFile content should be provided."));
        }
        content = content.trim();
        
        try {
            LocalFile oldLocalFile = LocalFileManager.getLocalFile(name);
            if(oldLocalFile != null) {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Local file already exists."));
            }

            LocalFile localFile = new LocalFile();
            localFile.name = name;
            ByteArrayInputStream input = new ByteArrayInputStream(content.getBytes());
            LocalFileManager.getInstance().createLocalFile(localFile, input);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("LocalFile has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create local file", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
    		@FormParam("id") int id,
            @FormParam("name") String name, 
            @FormParam("content") String content,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("LocalFile name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("LocalFile content should be provided."));
        }
        content = content.trim();
        
        try {
            LocalFile oldLocalFile = LocalFileManager.getInstance().getLocalFile(id);
            if(oldLocalFile != null) {
            	LocalFile localFile = new LocalFile();
            	localFile.id = oldLocalFile.id;
            	localFile.name = name;
            	ByteArrayInputStream input = new ByteArrayInputStream(content.getBytes());
            	LocalFileManager.getInstance().updateLocalFile(localFile, input);
            	
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("LocalFile has been successfully updated."));
            }
            else {
            	return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("No such local file could be found."));
            }
        }
        catch(Exception e) {
            logger.error("failed to update local file", e);
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
            LocalFile localFile = LocalFileManager.getInstance().getLocalFile(id);
            if(localFile != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("LocalFile has been successfully fetched.");
                result.data = localFile;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("LocalFile does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get local file", e);
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
            List<LocalFile> localFiles = LocalFileManager.getInstance().getLocalFiles();
            Collections.sort(localFiles, new Comparator<LocalFile>(){

				@Override
				public int compare(LocalFile o1, LocalFile o2) {
					return Integer.compare(o1.id, o2.id);
				}
            	
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("LocalFiles have been successfully fetched.");
            result.list = localFiles;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get local files", e);
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
        	LocalFileManager.getInstance().deleteLocalFile(id);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("LocalFile has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete local file", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
