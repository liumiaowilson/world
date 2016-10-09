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
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.RoleDetail;

@Path("role_detail")
public class RoleDetailAPI {
    private static final Logger logger = Logger.getLogger(RoleDetailAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("roleId") int roleId, 
            @FormParam("roleAttrId") int roleAttrId, 
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("RoleDetail content should be provided."));
        }
        content = content.trim();
        
        try {
            RoleDetail detail = new RoleDetail();
            detail.roleId = roleId;
            detail.roleAttrId = roleAttrId;
            detail.content = content;
            RoleDetailManager.getInstance().createRoleDetail(detail);
            
            Event event = new Event();
            event.type = EventType.CreateRoleDetail;
            event.data.put("data", detail);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("RoleDetail has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create role detail", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("roleId") int roleId, 
            @FormParam("roleAttrId") int roleAttrId, 
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("RoleDetail content should be provided."));
        }
        content = content.trim();
        
        try {
            RoleDetail oldDetail = RoleDetailManager.getInstance().getRoleDetail(id);
            
            RoleDetail detail = new RoleDetail();
            detail.id = id;
            detail.roleId = roleId;
            detail.roleAttrId = roleAttrId;
            detail.content = content;
            RoleDetailManager.getInstance().updateRoleDetail(detail);
            
            Event event = new Event();
            event.type = EventType.UpdateRoleDetail;
            event.data.put("old_data", oldDetail);
            event.data.put("new_data", detail);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("RoleDetail has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update role detail", e);
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
            RoleDetail detail = RoleDetailManager.getInstance().getRoleDetail(id);
            if(detail != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("RoleDetail has been successfully fetched.");
                result.data = detail;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("RoleDetail does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get role detail", e);
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
            List<RoleDetail> details = RoleDetailManager.getInstance().getRoleDetails();
            
            APIResult result = APIResultUtils.buildOKAPIResult("RoleDetails have been successfully fetched.");
            result.list = details;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get role details", e);
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
            RoleDetail detail = RoleDetailManager.getInstance().getRoleDetail(id);
            
            RoleDetailManager.getInstance().deleteRoleDetail(id);
            
            Event event = new Event();
            event.type = EventType.DeleteRoleDetail;
            event.data.put("data", detail);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("RoleDetail has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete role detail", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
