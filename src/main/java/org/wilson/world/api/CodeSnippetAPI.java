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
import org.wilson.world.manager.CodeSnippetManager;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.CodeSnippet;

@Path("code_snippet")
public class CodeSnippetAPI {
    private static final Logger logger = Logger.getLogger(CodeSnippetAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("languageId") int languageId, 
            @FormParam("templateId") int templateId,
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
        
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("CodeSnippet content should be provided."));
        }
        content = content.trim();
        
        try {
        	CodeSnippet snippet = new CodeSnippet();
            snippet.languageId = languageId;
            snippet.templateId = templateId;
            snippet.content = content;
            CodeSnippetManager.getInstance().createCodeSnippet(snippet);
            
            Event event = new Event();
            event.type = EventType.CreateCodeSnippet;
            event.data.put("data", snippet);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("CodeSnippet has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create code snippet", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("languageId") int languageId, 
            @FormParam("templateId") int templateId,
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
        
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("CodeSnippet content should be provided."));
        }
        content = content.trim();
        
        try {
        	CodeSnippet oldSnippet = CodeSnippetManager.getInstance().getCodeSnippet(id);
            
        	CodeSnippet snippet = new CodeSnippet();
            snippet.id = id;
            snippet.languageId = languageId;
            snippet.templateId = templateId;
            snippet.content = content;
            CodeSnippetManager.getInstance().updateCodeSnippet(snippet);
            
            Event event = new Event();
            event.type = EventType.UpdateCodeSnippet;
            event.data.put("old_data", oldSnippet);
            event.data.put("new_data", snippet);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("CodeSnippet has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update code snippet", e);
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
        	CodeSnippet snippet = CodeSnippetManager.getInstance().getCodeSnippet(id);
            if(snippet != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("CodeSnippet has been successfully fetched.");
                result.data = snippet;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("CodeSnippet does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get code snippet", e);
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
            List<CodeSnippet> snippets = CodeSnippetManager.getInstance().getCodeSnippets();
            
            APIResult result = APIResultUtils.buildOKAPIResult("CodeSnippets have been successfully fetched.");
            result.list = snippets;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get code snippets", e);
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
        	CodeSnippet snippet = CodeSnippetManager.getInstance().getCodeSnippet(id);
            
        	CodeSnippetManager.getInstance().deleteCodeSnippet(id);
            
            Event event = new Event();
            event.type = EventType.DeleteCodeSnippet;
            event.data.put("data", snippet);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("CodeSnippet has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete code snippet", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
