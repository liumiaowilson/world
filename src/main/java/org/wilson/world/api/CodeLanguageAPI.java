package org.wilson.world.api;

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
import org.wilson.world.manager.CodeLanguageManager;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.CodeLanguage;

@Path("code_language")
public class CodeLanguageAPI {
    private static final Logger logger = Logger.getLogger(CodeLanguageAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("type") String type,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("CodeLanguage name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(type)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("CodeLanguage type should be provided."));
        }
        type = type.trim();
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("CodeLanguage content should be provided."));
        }
        content = content.trim();
        
        try {
        	CodeLanguage lang = new CodeLanguage();
            lang.name = name;
            lang.type = type;
            lang.content = content;
            CodeLanguageManager.getInstance().createCodeLanguage(lang);
            
            Event event = new Event();
            event.type = EventType.CreateCodeLanguage;
            event.data.put("data", lang);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("CodeLanguage has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create code language", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("name") String name, 
            @FormParam("type") String type,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("CodeLanguage name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(type)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("CodeLanguage type should be provided."));
        }
        type = type.trim();
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("CodeLanguage content should be provided."));
        }
        content = content.trim();
        
        try {
        	CodeLanguage oldLang = CodeLanguageManager.getInstance().getCodeLanguage(id);
            
        	CodeLanguage lang = new CodeLanguage();
            lang.id = id;
            lang.name = name;
            lang.type = type;
            lang.content = content;
            CodeLanguageManager.getInstance().updateCodeLanguage(lang);
            
            Event event = new Event();
            event.type = EventType.UpdateCodeLanguage;
            event.data.put("old_data", oldLang);
            event.data.put("new_data", lang);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("CodeLanguage has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update code language", e);
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
        	CodeLanguage lang = CodeLanguageManager.getInstance().getCodeLanguage(id);
            if(lang != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("CodeLanguage has been successfully fetched.");
                result.data = lang;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("CodeLanguage does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get code language", e);
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
            List<CodeLanguage> langs = CodeLanguageManager.getInstance().getCodeLanguages();
            
            APIResult result = APIResultUtils.buildOKAPIResult("CodeLanguages have been successfully fetched.");
            result.list = langs;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get code languages", e);
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
        	CodeLanguage lang = CodeLanguageManager.getInstance().getCodeLanguage(id);
            
        	CodeLanguageManager.getInstance().deleteCodeLanguage(id);
            
            Event event = new Event();
            event.type = EventType.DeleteCodeLanguage;
            event.data.put("data", lang);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("CodeLanguage has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete code language", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
