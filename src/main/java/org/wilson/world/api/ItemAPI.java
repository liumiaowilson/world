package org.wilson.world.api;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
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
import org.wilson.world.manager.CacheManager;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.ItemManager;
import org.wilson.world.manager.MarkManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;

@Path("item")
public class ItemAPI {
    private static final Logger logger = Logger.getLogger(ItemAPI.class);
    
    @GET
    @Path("/list_table_names")
    @Produces("application/json")
    public Response listTableNames(
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
        
        List<String> names = ItemManager.getInstance().getItemTableNames();
        APIResult result = APIResultUtils.buildOKAPIResult("Item table names are successfully fetched.");
        result.list = names;
        
        return APIResultUtils.buildJSONResponse(result);
    }
    
    @GET
    @Path("/clear_table")
    @Produces("application/json")
    public Response clearTable(
            @QueryParam("name") String name,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Table name is needed."));
        }
        
        try {
            ItemManager.getInstance().clearTable(name);
            
            Event event = new Event();
            event.type = EventType.ClearTable;
            event.data.put("names", Arrays.asList(name));
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Table has been successfully cleared."));
        }
        catch(Exception e) {
            logger.error("failed to clear table!", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Failed to clear table."));
        }
    }
    
    @GET
    @Path("/clear_tables")
    @Produces("application/json")
    public Response clearTables(
            @QueryParam("names") String names,
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
        
        if(StringUtils.isBlank(names)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Table names are needed."));
        }
        
        String [] items = names.split(",");
        
        try {
            ItemManager.getInstance().clearTables(Arrays.asList(items));

            Event event = new Event();
            event.type = EventType.ClearTable;
            event.data.put("names", Arrays.asList(items));
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Tables have been successfully cleared."));
        }
        catch(Exception e) {
            logger.error("failed to clear tables!", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Failed to clear tables."));
        }
    }
    
    @GET
    @Path("/reload_cache")
    @Produces("application/json")
    public Response reloadCache(
            @QueryParam("names") String names,
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
        
        if(StringUtils.isBlank(names)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Cache names are needed."));
        }
        
        String [] items = names.split(",");
        
        try {
            for(String item : items) {
                CacheManager.getInstance().reloadCache(item);
            }
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Caches have been successfully reloaded."));
        }
        catch(Exception e) {
            logger.error("failed to reload caches!", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Failed to reload caches."));
        }
    }
    
    @GET
    @Path("/mark")
    @Produces("application/json")
    public Response mark(
            @QueryParam("type") String type,
            @QueryParam("id") String id,
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
        
        if(StringUtils.isBlank(type)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Item type is needed."));
        }
        type = type.trim();
        
        if(StringUtils.isBlank(id)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Item id is needed."));
        }
        id = id.trim();
        
        MarkManager.getInstance().mark(type, id);
        
        return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Item successfully marked."));
    }
    
    @GET
    @Path("/unmark")
    @Produces("application/json")
    public Response unmark(
            @QueryParam("type") String type,
            @QueryParam("id") String id,
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
        
        if(StringUtils.isBlank(type)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Item type is needed."));
        }
        type = type.trim();
        
        if(StringUtils.isBlank(id)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Item id is needed."));
        }
        id = id.trim();
        
        MarkManager.getInstance().unmark(type, id);
        
        return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Item successfully unmarked."));
    }
    
    @GET
    @Path("/list_marked")
    @Produces("application/json")
    public Response listMarked(
            @QueryParam("type") String type,
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
        
        if(StringUtils.isBlank(type)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Item type is needed."));
        }
        type = type.trim();
        
        List<String> ids = MarkManager.getInstance().getMarked(type);
        APIResult result = APIResultUtils.buildOKAPIResult("Marked items are listed.");
        result.list = ids;
        
        return APIResultUtils.buildJSONResponse(result);
    }
}
