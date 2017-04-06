package org.wilson.world.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.api.util.APIResultUtils;
import org.wilson.world.file.RemoteFile;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.RemoteFileManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;

@Path("remote_file")
public class RemoteFileAPI {
    private static final Logger logger = Logger.getLogger(RemoteFileAPI.class);
    
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("RemoteFile name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("RemoteFile content should be provided."));
        }
        content = content.trim();
        
        try {
            RemoteFile oldRemoteFile = RemoteFileManager.getInstance().getRemoteFile(name);
            if(oldRemoteFile != null) {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Remote file already exists."));
            }

        	RemoteFile remoteFile = new RemoteFile();
            remoteFile.name = name;
            ByteArrayInputStream input = new ByteArrayInputStream(content.getBytes());
            RemoteFileManager.getInstance().createRemoteFile(remoteFile, input);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("RemoteFile has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create remote file", e);
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("RemoteFile name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("RemoteFile content should be provided."));
        }
        content = content.trim();
        
        try {
        	RemoteFile oldRemoteFile = RemoteFileManager.getInstance().getRemoteFile(id);
            if(oldRemoteFile != null) {
            	RemoteFile remoteFile = new RemoteFile();
            	remoteFile.id = oldRemoteFile.id;
            	remoteFile.name = name;
            	ByteArrayInputStream input = new ByteArrayInputStream(content.getBytes());
            	RemoteFileManager.getInstance().updateRemoteFile(remoteFile, input);
            	
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("RemoteFile has been successfully updated."));
            }
            else {
            	return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("No such remote file could be found."));
            }
        }
        catch(Exception e) {
            logger.error("failed to update remote file", e);
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
        	RemoteFile remoteFile = RemoteFileManager.getInstance().getRemoteFile(id);
            if(remoteFile != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("RemoteFile has been successfully fetched.");
                result.data = remoteFile;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("RemoteFile does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get remote file", e);
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
            List<RemoteFile> remoteFiles = RemoteFileManager.getInstance().getRemoteFiles();
            Collections.sort(remoteFiles, new Comparator<RemoteFile>(){

				@Override
				public int compare(RemoteFile o1, RemoteFile o2) {
					return Integer.compare(o1.id, o2.id);
				}
            	
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("RemoteFiles have been successfully fetched.");
            result.list = remoteFiles;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get remote files", e);
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
        	RemoteFileManager.getInstance().deleteRemoteFile(id);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("RemoteFile has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete remote file", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/get_file")
    public Response getFile(
            @QueryParam("token") String token,
            @Context HttpHeaders headers,
            @Context HttpServletRequest request,
            @Context UriInfo uriInfo)
    {
        StreamingOutput fileStream =  new StreamingOutput() 
        {
            @Override
            public void write(java.io.OutputStream output) throws IOException, WebApplicationException 
            {
                try
                {
                    String url = ConfigManager.getInstance().getDataDir() + RemoteFileManager.TMP_FILE_NAME;
                    java.nio.file.Path path = Paths.get(url);
                    byte[] data = Files.readAllBytes(path);
                    output.write(data);
                    output.flush();
                } 
                catch (Exception e) 
                {
                    logger.error("failed to get file", e);
                    throw new WebApplicationException();
                }
            }
        };
        return Response
                .ok(fileStream, MediaType.TEXT_PLAIN)
                .header("content-disposition","attachment; filename = " + RemoteFileManager.TMP_FILE_NAME)
                .build();
    }
}
