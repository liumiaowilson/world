package org.wilson.world.api;

import java.net.URISyntaxException;
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
import org.wilson.world.manager.DataManager;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.ExpenseItemManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.ExpenseItem;

@Path("expense_item")
public class ExpenseItemAPI {
    private static final Logger logger = Logger.getLogger(ExpenseItemAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("type") String type,
            @FormParam("description") String description,
            @FormParam("amount") int amount,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Item name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(type)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Item type should be provided."));
        }
        type = type.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Item description should be provided."));
        }
        description = description.trim();
        
        try {
            ExpenseItem item = new ExpenseItem();
            item.name = name;
            item.type = type;
            item.description = description;
            item.amount = amount;
            item.time = System.currentTimeMillis();
            ExpenseItemManager.getInstance().createExpenseItem(item);
            
            Event event = new Event();
            event.type = EventType.CreateExpenseItem;
            event.data.put("data", item);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Item has been successfully created."));
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
            @FormParam("name") String name, 
            @FormParam("type") String type,
            @FormParam("description") String description,
            @FormParam("amount") int amount,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Item name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(type)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Item type should be provided."));
        }
        type = type.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Item description should be provided."));
        }
        description = description.trim();
        
        try {
            ExpenseItem oldItem = ExpenseItemManager.getInstance().getExpenseItem(id);
            
            ExpenseItem item = new ExpenseItem();
            item.id = id;
            item.name = name;
            item.type = type;
            item.description = description;
            item.amount = amount;
            item.time = oldItem.time;
            ExpenseItemManager.getInstance().updateExpenseItem(item);
            
            Event event = new Event();
            event.type = EventType.UpdateExpenseItem;
            event.data.put("old_data", oldItem);
            event.data.put("new_data", item);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Item has been successfully updated."));
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
            ExpenseItem item = ExpenseItemManager.getInstance().getExpenseItem(id);
            if(item != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Item has been successfully fetched.");
                result.data = item;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Item does not exist."));
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
            List<ExpenseItem> items = ExpenseItemManager.getInstance().getExpenseItems();
            Collections.sort(items, new Comparator<ExpenseItem>(){

                @Override
                public int compare(ExpenseItem o1, ExpenseItem o2) {
                    return -(o1.id - o2.id);
                }
                
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("Items have been successfully fetched.");
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
            ExpenseItem item = ExpenseItemManager.getInstance().getExpenseItem(id);
            
            ExpenseItemManager.getInstance().deleteExpenseItem(id);
            
            Event event = new Event();
            event.type = EventType.DeleteExpenseItem;
            event.data.put("data", item);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Item has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete item", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/create_public")
    @Produces("application/json")
    public Response createPublic(
            @FormParam("key") String key,
            @FormParam("name") String name, 
            @FormParam("type") String type,
            @FormParam("amount") int amount,
            @Context HttpHeaders headers,
            @Context HttpServletRequest request,
            @Context UriInfo uriInfo) throws URISyntaxException {
        String k = DataManager.getInstance().getValue("public.key");
        if(k == null || !k.equals(key)) {
            return APIResultUtils.buildURLResponse(request, "public_error.jsp");
        }
        
        if(StringUtils.isBlank(name)) {
            return APIResultUtils.buildURLResponse(request, "public_error.jsp", "Name should be provided.");
        }
        name = name.trim();
        if(StringUtils.isBlank(type)) {
            return APIResultUtils.buildURLResponse(request, "public_error.jsp", "Type should be provided.");
        }
        type = type.trim();
        String description = name;
        
        try {
            ExpenseItem item = new ExpenseItem();
            item.name = name;
            item.type = type;
            item.description = description;
            item.amount = amount;
            item.time = System.currentTimeMillis();
            ExpenseItemManager.getInstance().createExpenseItem(item);
            
            Event event = new Event();
            event.type = EventType.CreateExpenseItem;
            event.data.put("data", item);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildURLResponse(request, "expense.jsp");
        }
        catch(Exception e) {
            logger.error("failed to create item", e);
            return APIResultUtils.buildURLResponse(request, "public_error.jsp", e.getMessage());
        }
    }
}
