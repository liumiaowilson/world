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
import org.wilson.world.manager.NovelRoleManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.NovelFragment;
import org.wilson.world.model.NovelRole;
import org.wilson.world.novel.NovelRoleInfo;

@Path("novel_role")
public class NovelRoleAPI {
    private static final Logger logger = Logger.getLogger(NovelRoleAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("description") String description,
            @FormParam("definition") String definition,
            @FormParam("image") String image,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("NovelRole name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("NovelRole description should be provided."));
        }
        description = description.trim();
        if(StringUtils.isBlank(definition)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("NovelRole definition should be provided."));
        }
        definition = definition.trim();
        
        try {
        	NovelRole role = new NovelRole();
            role.name = name;
            role.description = description;
            role.definition = definition;
            role.image = image;
            NovelRoleManager.getInstance().createNovelRole(role);
            
            Event event = new Event();
            event.type = EventType.CreateNovelRole;
            event.data.put("data", role);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("NovelRole has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create novel role", e);
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
            @FormParam("definition") String definition,
            @FormParam("image") String image,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("NovelRole name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("NovelRole description should be provided."));
        }
        description = description.trim();
        if(StringUtils.isBlank(definition)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("NovelRole definition should be provided."));
        }
        definition = definition.trim();
        
        try {
        	NovelRole oldRole = NovelRoleManager.getInstance().getNovelRole(id);
            
        	NovelRole role = new NovelRole();
            role.id = id;
            role.name = name;
            role.description = description;
            role.definition = definition;
            role.image = image;
            NovelRoleManager.getInstance().updateNovelRole(role);
            
            Event event = new Event();
            event.type = EventType.UpdateNovelRole;
            event.data.put("old_data", oldRole);
            event.data.put("new_data", role);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("NovelRole has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update novel role", e);
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
        	NovelRole role = NovelRoleManager.getInstance().getNovelRole(id);
            if(role != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("NovelRole has been successfully fetched.");
                result.data = role;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("NovelRole does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get novel role", e);
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
            List<NovelRole> roles = NovelRoleManager.getInstance().getNovelRoles();
            
            Collections.sort(roles, new Comparator<NovelRole>(){

				@Override
				public int compare(NovelRole o1, NovelRole o2) {
					return Integer.compare(o1.id, o2.id);
				}
            	
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("NovelRoles have been successfully fetched.");
            result.list = roles;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get novel roles", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/query")
    @Produces("application/json")
    public Response query(
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
        
        try {
            List<NovelRole> roles = NovelRoleManager.getInstance().getNovelRoles(script);
            
            Collections.sort(roles, new Comparator<NovelRole>(){

				@Override
				public int compare(NovelRole o1, NovelRole o2) {
					return Integer.compare(o1.id, o2.id);
				}
            	
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("NovelRoles have been successfully fetched.");
            result.list = roles;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get novel roles", e);
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
        	NovelRole role = NovelRoleManager.getInstance().getNovelRole(id);
            
        	NovelRoleManager.getInstance().deleteNovelRole(id);
            
            Event event = new Event();
            event.type = EventType.DeleteNovelRole;
            event.data.put("data", role);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("NovelRole has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete novel role", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/find_fragments")
    @Produces("application/json")
    public Response findFragments(
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
        	NovelRole role = NovelRoleManager.getInstance().getNovelRole(id);
        	if(role == null) {
        		return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("No such role could be found."));
        	}
        	
            List<NovelFragment> fragments = NovelRoleManager.getInstance().getStartNovelFragmentsFor(role);
            
            Collections.sort(fragments, new Comparator<NovelFragment>(){

				@Override
				public int compare(NovelFragment o1, NovelFragment o2) {
					return Integer.compare(o1.id, o2.id);
				}
            	
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("NovelFragments have been successfully fetched.");
            result.list = fragments;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get novel fragments", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/validate")
    @Produces("application/json")
    public Response validate(
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
            List<NovelRoleInfo> infos = NovelRoleManager.getInstance().validateAll();
            Collections.sort(infos, new Comparator<NovelRoleInfo>(){

				@Override
				public int compare(NovelRoleInfo o1, NovelRoleInfo o2) {
					return Integer.compare(o1.id, o2.id);
				}
            	
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("NovelRoleInfos have been successfully fetched.");
            result.list = infos;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get novel role infos", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
