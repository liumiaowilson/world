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
import org.wilson.world.manager.RoleDetailManager;
import org.wilson.world.manager.RoleManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.Role;
import org.wilson.world.model.RoleDetail;

@Path("role")
public class RoleAPI {
    private static final Logger logger = Logger.getLogger(RoleAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("description") String description,
            @FormParam("attrIds") String attrIds,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Role name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Role description should be provided."));
        }
        description = description.trim();
        if(StringUtils.isBlank(attrIds)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Role attrIds should be provided."));
        }
        attrIds = attrIds.trim();
        
        try {
            Role role = new Role();
            role.name = name;
            role.description = description;
            role.attrIds = attrIds;
            RoleManager.getInstance().createRole(role);
            
            Event event = new Event();
            event.type = EventType.CreateRole;
            event.data.put("data", role);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Role has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create role", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update_detail")
    @Produces("application/json")
    public Response updateDetail(
            @FormParam("id") int id, 
            @FormParam("attrId") int attrId,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Content should be provided."));
        }
        content = content.trim();
        
        try {
            RoleDetail detail = RoleDetailManager.getInstance().getRoleDetail(id, attrId);
            if(detail == null) {
                detail = new RoleDetail();
                detail.roleId = id;
                detail.roleAttrId = attrId;
                detail.content = content;
                RoleDetailManager.getInstance().createRoleDetail(detail);

                Event event = new Event();
                event.type = EventType.CreateRoleDetail;
                event.data.put("data", detail);
                EventManager.getInstance().fireEvent(event);
            }
            else {
                RoleDetail newDetail = new RoleDetail();
                newDetail.id = detail.id;
                newDetail.roleId = id;
                newDetail.roleAttrId = attrId;
                newDetail.content = content;
                RoleDetailManager.getInstance().updateRoleDetail(newDetail);

                Event event = new Event();
                event.type = EventType.UpdateRoleDetail;
                event.data.put("old_data", detail);
                event.data.put("new_data", newDetail);
                EventManager.getInstance().fireEvent(event);
            }
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Role detail has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update role detail", e);
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
            @FormParam("attrIds") String attrIds,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Role name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Role description should be provided."));
        }
        description = description.trim();
        if(StringUtils.isBlank(attrIds)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Role attrIds should be provided."));
        }
        attrIds = attrIds.trim();
        
        try {
            Role oldRole = RoleManager.getInstance().getRole(id);
            
            Role role = new Role();
            role.id = id;
            role.name = name;
            role.description = description;
            role.attrIds = attrIds;
            RoleManager.getInstance().updateRole(role);
            
            Event event = new Event();
            event.type = EventType.UpdateRole;
            event.data.put("old_data", oldRole);
            event.data.put("new_data", role);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Role has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update role", e);
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
            Role role = RoleManager.getInstance().getRole(id);
            if(role != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Role has been successfully fetched.");
                result.data = role;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Role does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get role", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/get_attrs")
    @Produces("application/json")
    public Response getAttrs(
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
            Role role = RoleManager.getInstance().getRole(id);
            if(role != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Role has been successfully fetched.");
                result.list = role.attrs;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Role does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get role", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/get_content")
    @Produces("application/json")
    public Response getContent(
            @QueryParam("id") int id,
            @QueryParam("attrId") int attrId,
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
            Role role = RoleManager.getInstance().getRole(id);
            if(role != null) {
                List<RoleDetail> details = RoleDetailManager.getInstance().getRoleDetails(role.id);
                String content = "";
                for(RoleDetail detail : details) {
                    if(detail.roleAttrId == attrId) {
                        content = detail.content;
                    }
                }
                APIResult result = APIResultUtils.buildOKAPIResult("Content has been successfully fetched.");
                result.data = content;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Role does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get content", e);
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
            List<Role> roles = RoleManager.getInstance().getRoles();
            
            APIResult result = APIResultUtils.buildOKAPIResult("Roles have been successfully fetched.");
            result.list = roles;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get roles", e);
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
            Role role = RoleManager.getInstance().getRole(id);
            
            RoleManager.getInstance().deleteRole(id);
            
            Event event = new Event();
            event.type = EventType.DeleteRole;
            event.data.put("data", role);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Role has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete role", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
