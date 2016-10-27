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
import org.wilson.world.manager.LinkManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.Link;

@Path("link")
public class LinkAPI {
    private static final Logger logger = Logger.getLogger(LinkAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("label") String label,
            @FormParam("itemType") String itemType,
            @FormParam("itemId") int itemId,
            @FormParam("menuId") String menuId,
            @FormParam("url") String url,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Link name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(label)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Link label should be provided."));
        }
        label = label.trim();
        if(StringUtils.isBlank(itemType)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Link itemType should be provided."));
        }
        itemType = itemType.trim();
        
        try {
            Link link = new Link();
            link.name = name;
            link.label = label;
            link.itemType = itemType;
            link.itemId = itemId;
            link.menuId = menuId;
            if(!StringUtils.isBlank(url)) {
                link.menuId = url;
            }
            LinkManager.getInstance().createLink(link);
            
            Event event = new Event();
            event.type = EventType.CreateLink;
            event.data.put("data", link);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Link has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create link", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("name") String name, 
            @FormParam("label") String label,
            @FormParam("itemType") String itemType,
            @FormParam("itemId") int itemId,
            @FormParam("menuId") String menuId,
            @FormParam("url") String url,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Link name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(label)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Link label should be provided."));
        }
        label = label.trim();
        if(StringUtils.isBlank(itemType)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Link itemType should be provided."));
        }
        itemType = itemType.trim();
        
        try {
            Link oldLink = LinkManager.getInstance().getLink(id);
            
            Link link = new Link();
            link.id = id;
            link.name = name;
            link.label = label;
            link.itemType = itemType;
            link.itemId = itemId;
            link.menuId = menuId;
            if(!StringUtils.isBlank(url)) {
                link.menuId = url;
            }
            LinkManager.getInstance().updateLink(link);
            
            Event event = new Event();
            event.type = EventType.UpdateLink;
            event.data.put("old_data", oldLink);
            event.data.put("new_data", link);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Link has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update link", e);
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
            Link link = LinkManager.getInstance().getLink(id);
            if(link != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Link has been successfully fetched.");
                result.data = link;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Link does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get link", e);
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
            List<Link> links = LinkManager.getInstance().getLinks();
            
            APIResult result = APIResultUtils.buildOKAPIResult("Links have been successfully fetched.");
            result.list = links;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get links", e);
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
            Link link = LinkManager.getInstance().getLink(id);
            
            LinkManager.getInstance().deleteLink(id);
            
            Event event = new Event();
            event.type = EventType.DeleteLink;
            event.data.put("data", link);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Link has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete link", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/query")
    @Produces("application/json")
    public Response query(
    		@QueryParam("type") String type,
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
            List<Link> links = LinkManager.getInstance().getLinks(type, id);
            
            APIResult result = APIResultUtils.buildOKAPIResult("Links have been successfully queried.");
            result.list = links;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to query links", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
