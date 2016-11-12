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
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.NovelVariableManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.NovelVariable;

@Path("novel_variable")
public class NovelVariableAPI {
    private static final Logger logger = Logger.getLogger(NovelVariableAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("description") String description,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("NovelVariable name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("NovelVariable description should be provided."));
        }
        description = description.trim();
        
        try {
        	NovelVariable variable = new NovelVariable();
            variable.name = name;
            variable.description = description;
            NovelVariableManager.getInstance().createNovelVariable(variable);
            
            Event event = new Event();
            event.type = EventType.CreateNovelVariable;
            event.data.put("data", variable);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("NovelVariable has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create novel variable", e);
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("NovelVariable name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("NovelVariable description should be provided."));
        }
        description = description.trim();
        
        try {
        	NovelVariable oldVariable = NovelVariableManager.getInstance().getNovelVariable(id);
            
        	NovelVariable variable = new NovelVariable();
            variable.id = id;
            variable.name = name;
            variable.description = description;
            NovelVariableManager.getInstance().updateNovelVariable(variable);
            
            Event event = new Event();
            event.type = EventType.UpdateNovelVariable;
            event.data.put("old_data", oldVariable);
            event.data.put("new_data", variable);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("NovelVariable has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update novel variable", e);
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
        	NovelVariable variable = NovelVariableManager.getInstance().getNovelVariable(id);
            if(variable != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("NovelVariable has been successfully fetched.");
                result.data = variable;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("NovelVariable does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get novel variable", e);
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
            List<NovelVariable> variables = NovelVariableManager.getInstance().getNovelVariables();
            
            APIResult result = APIResultUtils.buildOKAPIResult("NovelVariables have been successfully fetched.");
            result.list = variables;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get novel variables", e);
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
        	NovelVariable variable = NovelVariableManager.getInstance().getNovelVariable(id);
            
        	NovelVariableManager.getInstance().deleteNovelVariable(id);
            
            Event event = new Event();
            event.type = EventType.DeleteNovelVariable;
            event.data.put("data", variable);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("NovelVariable has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete novel variable", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
