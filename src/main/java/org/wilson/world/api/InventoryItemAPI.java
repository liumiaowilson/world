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
import org.wilson.world.manager.CharManager;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.InventoryItemManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.InventoryItem;

@Path("inventory_item")
public class InventoryItemAPI {
    private static final Logger logger = Logger.getLogger(InventoryItemAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("itemId") int itemId, 
            @FormParam("price") int price, 
            @FormParam("amount") int amount, 
            @FormParam("status") String status, 
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
        
        if(StringUtils.isBlank(status)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Item name should be provided."));
        }
        status = status.trim();
        
        try {
            InventoryItem item = new InventoryItem();
            item.itemId = itemId;
            item.price = price;
            item.amount = amount;
            item.status = status;
            InventoryItemManager.getInstance().createInventoryItem(item);
            
            Event event = new Event();
            event.type = EventType.CreateInventoryItem;
            event.data.put("data", item);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Inventory item has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create item", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("itemId") int itemId, 
            @FormParam("price") int price, 
            @FormParam("amount") int amount, 
            @FormParam("status") String status, 
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
        
        if(StringUtils.isBlank(status)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Item name should be provided."));
        }
        status = status.trim();
        
        try {
            InventoryItem oldItem = InventoryItemManager.getInstance().getInventoryItem(id);
            
            InventoryItem item = new InventoryItem();
            item.id = id;
            item.itemId = itemId;
            item.price = price;
            item.amount = amount;
            item.status = status;
            InventoryItemManager.getInstance().updateInventoryItem(item);
            
            Event event = new Event();
            event.type = EventType.UpdateInventoryItem;
            event.data.put("old_data", oldItem);
            event.data.put("new_data", item);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Inventory item has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update item", e);
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
            InventoryItem item = InventoryItemManager.getInstance().getInventoryItem(id);
            if(item != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Inventory item has been successfully fetched.");
                result.data = item;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Inventory item does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get item", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/search")
    @Produces("application/json")
    public Response search(
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
            int coins = CharManager.getInstance().getCoins();
            if(coins <= 0) {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("No enough coins to continue the search."));
            }
            coins -= 1;
            CharManager.getInstance().setCoins(coins);
            
            InventoryItem item = InventoryItemManager.getInstance().search();
            if(item != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Successfully gained an inventory item in the search.");
                result.data = item;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Failed to find an inventory item in the search."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get item", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/use")
    @Produces("application/json")
    public Response use(
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
            InventoryItem item = InventoryItemManager.getInstance().getInventoryItem(id);
            if(item != null) {
                String ret = InventoryItemManager.getInstance().useInventoryItem(item.id);
                if(StringUtils.isBlank(ret)) {
                    APIResult result = APIResultUtils.buildOKAPIResult("Inventory item used successfully.");
                    return APIResultUtils.buildJSONResponse(result);
                }
                else {
                    APIResult result = APIResultUtils.buildErrorAPIResult(ret);
                    return APIResultUtils.buildJSONResponse(result);
                }
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Inventory item does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get item", e);
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
            List<InventoryItem> items = InventoryItemManager.getInstance().getInventoryItems();
            
            Collections.sort(items, new Comparator<InventoryItem>(){

                @Override
                public int compare(InventoryItem o1, InventoryItem o2) {
                    int ret = o1.type.compareTo(o2.type);
                    if(ret == 0) {
                        return o1.name.compareTo(o2.name);
                    }
                    else {
                        return ret;
                    }
                }
                
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("Inventory items have been successfully fetched.");
            result.list = items;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get items", e);
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
            InventoryItem item = InventoryItemManager.getInstance().getInventoryItem(id);
            
            InventoryItemManager.getInstance().deleteInventoryItem(id);
            
            Event event = new Event();
            event.type = EventType.DeleteInventoryItem;
            event.data.put("data", item);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Inventory item has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete item", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
