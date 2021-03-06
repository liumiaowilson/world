package org.wilson.world.api;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.*;

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
import org.wilson.world.event.Event;
import org.wilson.world.event.EventType;
import org.wilson.world.java.RunJavaInfo;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.JavaFileManager;
import org.wilson.world.manager.JavaManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.JavaFile;
import org.wilson.world.util.FormatUtils;

@Path("java_file")
public class JavaFileAPI {
    private static final Logger logger = Logger.getLogger(JavaFileAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("description") String description,
            @FormParam("source") String source,
            @FormParam("script") String script,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("JavaFile name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("JavaFile description should be provided."));
        }
        description = description.trim();
        if(StringUtils.isBlank(source)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("JavaFile source should be provided."));
        }
        source = source.trim();
        
        try {
        	JavaFile file = new JavaFile();
            file.name = name;
            file.description = description;
            file.source = source;
            file.script = script;
            JavaFileManager.getInstance().createJavaFile(file);
            
            Event event = new Event();
            event.type = EventType.CreateJavaFile;
            event.data.put("data", file);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("JavaFile has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create java file", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("name") String name, 
            @FormParam("description") String description,
            @FormParam("source") String source,
            @FormParam("script") String script,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("JavaFile name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("JavaFile description should be provided."));
        }
        description = description.trim();
        if(StringUtils.isBlank(source)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("JavaFile source should be provided."));
        }
        source = source.trim();
        
        try {
        	JavaFile oldFile = JavaFileManager.getInstance().getJavaFile(id);
            
        	JavaFile file = new JavaFile();
            file.id = id;
            file.name = name;
            file.description = description;
            file.source = source;
            file.script = script;
            JavaFileManager.getInstance().updateJavaFile(file);
            
            Event event = new Event();
            event.type = EventType.UpdateJavaFile;
            event.data.put("old_data", oldFile);
            event.data.put("new_data", file);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("JavaFile has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update java file", e);
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
        	JavaFile file = JavaFileManager.getInstance().getJavaFile(id, false);
            if(file != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("JavaFile has been successfully fetched.");
                result.data = file;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("JavaFile does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get java file", e);
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
            List<JavaFile> files = JavaFileManager.getInstance().getJavaFiles();
            Collections.sort(files, new Comparator<JavaFile>(){

				@Override
				public int compare(JavaFile o1, JavaFile o2) {
					return Integer.compare(o1.id, o2.id);
				}
            	
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("JavaFiles have been successfully fetched.");
            result.list = files;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get java files", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }

    @GET
    @Path("/list_cached")
    @Produces("application/json")
    public Response listCached(
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
            List<JavaFile> files = new ArrayList<JavaFile>();
            for(JavaFile file : JavaFileManager.getInstance().getCachedJavaFiles().values()) {
                JavaFile simpleFile = new JavaFile();
                simpleFile.id = file.id;
                simpleFile.name = file.name;
                files.add(simpleFile);
            }
            Collections.sort(files, new Comparator<JavaFile>(){

                @Override
                public int compare(JavaFile o1, JavaFile o2) {
                    return Integer.compare(o1.id, o2.id);
                }
                
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("CachedJavaFiles have been successfully fetched.");
            result.list = files;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get cached java files", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }

    @GET
    @Path("/list_backup")
    @Produces("application/json")
    public Response listBackup(
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
            List<JavaFile> files = new ArrayList<JavaFile>();
            for(JavaFile file : JavaFileManager.getInstance().getBackupJavaFiles().values()) {
                JavaFile simpleFile = new JavaFile();
                simpleFile.id = file.id;
                simpleFile.name = file.name;
                files.add(simpleFile);
            }
            Collections.sort(files, new Comparator<JavaFile>(){

                @Override
                public int compare(JavaFile o1, JavaFile o2) {
                    return Integer.compare(o1.id, o2.id);
                }
                
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("BackupJavaFiles have been successfully fetched.");
            result.list = files;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get backup java files", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }

    @GET
    @Path("/clear_cache")
    @Produces("application/json")
    public Response clearCache(
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
            JavaFileManager.getInstance().getCachedJavaFiles().clear();
            
            APIResult result = APIResultUtils.buildOKAPIResult("CachedJavaFiles have been successfully cleared.");
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to clear cached java files", e);
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
        	JavaFile file = JavaFileManager.getInstance().getJavaFile(id);
            
        	JavaFileManager.getInstance().deleteJavaFile(id);
            
            Event event = new Event();
            event.type = EventType.DeleteJavaFile;
            event.data.put("data", file);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("JavaFile has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete java file", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }

    @GET
    @Path("/restore_backup")
    @Produces("application/json")
    public Response restoreBackup(
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
            JavaFile file = JavaFileManager.getInstance().getBackupJavaFiles().get(id);
            if(file == null) {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("No such backup java file could be found."));
            }

            JavaFile oldFile = JavaFileManager.getInstance().getJavaFile(file.id);
            if(oldFile == null) {
                JavaFileManager.getInstance().createJavaFile(file);
            }
            else {
                JavaFileManager.getInstance().updateJavaFile(file);
            }
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("JavaFile has been successfully restored."));
        }
        catch(Exception e) {
            logger.error("failed to restore java file", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }

    @GET
    @Path("/delete_cache")
    @Produces("application/json")
    public Response deleteCache(
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
            JavaFileManager.getInstance().getCachedJavaFiles().remove(id);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("CachedJavaFile has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete cached java file", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/run")
    @Produces("application/json")
    public Response run(
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
        	JavaFile file = JavaFileManager.getInstance().getJavaFile(id);
            if(file != null) {
            	RunJavaInfo info = JavaManager.getInstance().run(file.getSource(), false, false);
            	String message = info.getMessage();
            	message = FormatUtils.escapeHtml(message);
            	if(info.isSuccessful) {
                    APIResult result = APIResultUtils.buildOKAPIResult(message);
                    return APIResultUtils.buildJSONResponse(result);
            	}
            	else {
            		return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(message));
            	}
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("JavaFile does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get java file", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/compile_all")
    @Produces("application/json")
    public Response compileAll(
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
            JavaFileManager.getInstance().compileAll();
            
            APIResult result = APIResultUtils.buildOKAPIResult("JavaFiles have been successfully compiled.");
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to compile java files", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
