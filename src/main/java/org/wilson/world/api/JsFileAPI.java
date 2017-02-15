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
import org.wilson.world.event.Event;
import org.wilson.world.event.EventType;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.JsFileManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.JsFile;

@Path("js_file")
public class JsFileAPI {
    private static final Logger logger = Logger.getLogger(JsFileAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("description") String description,
            @FormParam("status") String status,
            @FormParam("source") String source,
            @FormParam("test") String test,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("JsFile name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("JsFile description should be provided."));
        }
        description = description.trim();
        if(StringUtils.isBlank(status)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("JsFile status should be provided."));
        }
        status = status.trim();
        if(StringUtils.isBlank(source)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("JsFile source should be provided."));
        }
        source = source.trim();
        
        try {
        	JsFile file = new JsFile();
            file.name = name;
            file.description = description;
            file.status = status;
            file.source = source;
            file.test = test;
            JsFileManager.getInstance().createJsFile(file);
            
            Event event = new Event();
            event.type = EventType.CreateJsFile;
            event.data.put("data", file);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("JsFile has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create js file", e);
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
            @FormParam("status") String status,
            @FormParam("source") String source,
            @FormParam("test") String test,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("JsFile name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("JsFile description should be provided."));
        }
        description = description.trim();
        if(StringUtils.isBlank(status)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("JsFile status should be provided."));
        }
        status = status.trim();
        if(StringUtils.isBlank(source)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("JsFile source should be provided."));
        }
        source = source.trim();
        
        try {
        	JsFile oldFile = JsFileManager.getInstance().getJsFile(id);
            
        	JsFile file = new JsFile();
            file.id = id;
            file.name = name;
            file.description = description;
            file.status = status;
            file.source = source;
            file.test = test;
            JsFileManager.getInstance().updateJsFile(file);
            
            Event event = new Event();
            event.type = EventType.UpdateJsFile;
            event.data.put("old_data", oldFile);
            event.data.put("new_data", file);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("JsFile has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update js file", e);
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
        	JsFile file = JsFileManager.getInstance().getJsFile(id, false);
            if(file != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("JsFile has been successfully fetched.");
                result.data = file;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("JsFile does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get js file", e);
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
            List<JsFile> files = JsFileManager.getInstance().getJsFiles();
            Collections.sort(files, new Comparator<JsFile>() {

				@Override
				public int compare(JsFile o1, JsFile o2) {
					return Integer.compare(o1.id, o2.id);
				}
            	
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("JsFiles have been successfully fetched.");
            result.list = files;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get js files", e);
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
        	JsFile file = JsFileManager.getInstance().getJsFile(id);
            
        	JsFileManager.getInstance().deleteJsFile(id);
            
            Event event = new Event();
            event.type = EventType.DeleteJsFile;
            event.data.put("data", file);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("JsFile has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete js file", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/validate")
    @Produces("application/json")
    public Response validate(
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
        	JsFile file = JsFileManager.getInstance().getJsFile(id, false);
            if(file != null) {
            	String ret = JsFileManager.getInstance().validateJsFile(file);
            	if(ret == null) {
            		APIResult result = APIResultUtils.buildOKAPIResult("JsFile has been successfully validated.");
                    return APIResultUtils.buildJSONResponse(result);
            	}
            	else {
            		return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(ret));
            	}
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("JsFile does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get js file", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
