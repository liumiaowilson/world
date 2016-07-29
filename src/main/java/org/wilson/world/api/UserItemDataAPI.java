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
import org.wilson.world.manager.SecManager;
import org.wilson.world.manager.UserItemDataManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.UserItemData;

@Path("user_item_data")
public class UserItemDataAPI {
    private static final Logger logger = Logger.getLogger(UserItemDataAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("type") String type,
            @FormParam("description") String description,
            @FormParam("effect") String effect,
            @FormParam("value") int value,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("User item data name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(type)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("User item data type should be provided."));
        }
        type = type.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("User item data description should be provided."));
        }
        description = description.trim();
        if(StringUtils.isBlank(effect)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("User item data effect should be provided."));
        }
        effect = effect.trim();
        
        try {
            UserItemData data = new UserItemData();
            data.name = name;
            data.type = type;
            data.description = description;
            data.effect = effect;
            data.value = value;
            UserItemDataManager.getInstance().createUserItemData(data);
            
            Event event = new Event();
            event.type = EventType.CreateUserItemData;
            event.data.put("data", data);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("UserItemData has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create data", e);
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
            @FormParam("description") String description,
            @FormParam("effect") String effect,
            @FormParam("value") int value,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("User item data name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(type)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("User item data type should be provided."));
        }
        type = type.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("User item data description should be provided."));
        }
        description = description.trim();
        if(StringUtils.isBlank(effect)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("User item data effect should be provided."));
        }
        effect = effect.trim();
        
        try {
            UserItemData oldData = UserItemDataManager.getInstance().getUserItemData(id);
            
            UserItemData data = new UserItemData();
            data.id = id;
            data.name = name;
            data.type = type;
            data.description = description;
            data.effect = effect;
            data.value = value;
            UserItemDataManager.getInstance().updateUserItemData(data);
            
            Event event = new Event();
            event.type = EventType.UpdateUserItemData;
            event.data.put("old_data", oldData);
            event.data.put("new_data", data);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("UserItemData has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update data", e);
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
            UserItemData data = UserItemDataManager.getInstance().getUserItemData(id);
            if(data != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("UserItemData has been successfully fetched.");
                result.data = data;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("UserItemData does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get data", e);
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
            List<UserItemData> datas = UserItemDataManager.getInstance().getUserItemDatas();
            
            APIResult result = APIResultUtils.buildOKAPIResult("UserItemDatas have been successfully fetched.");
            result.list = datas;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get datas", e);
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
            UserItemData data = UserItemDataManager.getInstance().getUserItemData(id);
            
            UserItemDataManager.getInstance().deleteUserItemData(id);
            
            Event event = new Event();
            event.type = EventType.DeleteUserItemData;
            event.data.put("data", data);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("UserItemData has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete data", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
