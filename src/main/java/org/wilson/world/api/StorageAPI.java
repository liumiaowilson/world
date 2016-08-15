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
import org.wilson.world.manager.StorageManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.Storage;

@Path("storage")
public class StorageAPI {
    private static final Logger logger = Logger.getLogger(StorageAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("description") String description,
            @FormParam("url") String url,
            @FormParam("key") String key,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Storage name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Storage description should be provided."));
        }
        description = description.trim();
        if(StringUtils.isBlank(url)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Storage url should be provided."));
        }
        url = url.trim();
        if(StringUtils.isBlank(key)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Storage key should be provided."));
        }
        key = key.trim();
        
        try {
            Storage storage = new Storage();
            storage.name = name;
            storage.description = description;
            storage.url = url;
            storage.key = key;
            StorageManager.getInstance().createStorage(storage);
            
            Event event = new Event();
            event.type = EventType.CreateStorage;
            event.data.put("data", storage);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Storage has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create storage", e);
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
            @FormParam("url") String url,
            @FormParam("key") String key,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Storage name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Storage description should be provided."));
        }
        description = description.trim();
        if(StringUtils.isBlank(url)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Storage url should be provided."));
        }
        url = url.trim();
        if(StringUtils.isBlank(key)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Storage key should be provided."));
        }
        key = key.trim();
        
        try {
            Storage oldStorage = StorageManager.getInstance().getStorage(id);
            
            Storage storage = new Storage();
            storage.id = id;
            storage.name = name;
            storage.description = description;
            storage.url = url;
            storage.key = key;
            StorageManager.getInstance().updateStorage(storage);
            
            Event event = new Event();
            event.type = EventType.UpdateStorage;
            event.data.put("old_data", oldStorage);
            event.data.put("new_data", storage);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Storage has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update storage", e);
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
            Storage storage = StorageManager.getInstance().getStorage(id);
            if(storage != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Storage has been successfully fetched.");
                result.data = storage;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Storage does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get storage", e);
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
            List<Storage> storages = StorageManager.getInstance().getStorages();
            
            APIResult result = APIResultUtils.buildOKAPIResult("Storages have been successfully fetched.");
            result.list = storages;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get storages", e);
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
            Storage storage = StorageManager.getInstance().getStorage(id);
            
            StorageManager.getInstance().deleteStorage(id);
            
            Event event = new Event();
            event.type = EventType.DeleteStorage;
            event.data.put("data", storage);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Storage has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete storage", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
