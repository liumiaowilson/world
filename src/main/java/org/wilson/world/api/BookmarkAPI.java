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
import org.wilson.world.manager.BookmarkManager;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.Bookmark;

@Path("bookmark")
public class BookmarkAPI {
    private static final Logger logger = Logger.getLogger(BookmarkAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("group") String group, 
            @FormParam("content") String content,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Bookmark name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(group)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Bookmark group should be provided."));
        }
        group = group.trim();
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Bookmark content should be provided."));
        }
        content = content.trim();
        if(StringUtils.isBlank(url)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Bookmark url should be provided."));
        }
        url = url.trim();
        
        try {
        	Bookmark bookmark = new Bookmark();
            bookmark.name = name;
            bookmark.group = group;
            bookmark.content = content;
            bookmark.url = url;
            BookmarkManager.getInstance().createBookmark(bookmark);
            
            Event event = new Event();
            event.type = EventType.CreateBookmark;
            event.data.put("data", bookmark);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Bookmark has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create bookmark", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("name") String name, 
            @FormParam("group") String group, 
            @FormParam("content") String content,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Bookmark name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(group)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Bookmark group should be provided."));
        }
        group = group.trim();
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Bookmark content should be provided."));
        }
        content = content.trim();
        if(StringUtils.isBlank(url)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Bookmark url should be provided."));
        }
        url = url.trim();
        
        try {
        	Bookmark oldBookmark = BookmarkManager.getInstance().getBookmark(id);
            
        	Bookmark bookmark = new Bookmark();
            bookmark.id = id;
            bookmark.name = name;
            bookmark.group = group;
            bookmark.content = content;
            bookmark.url = url;
            BookmarkManager.getInstance().updateBookmark(bookmark);
            
            Event event = new Event();
            event.type = EventType.UpdateBookmark;
            event.data.put("old_data", oldBookmark);
            event.data.put("new_data", bookmark);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Bookmark has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update bookmark", e);
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
        	Bookmark bookmark = BookmarkManager.getInstance().getBookmark(id);
            if(bookmark != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Bookmark has been successfully fetched.");
                result.data = bookmark;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Bookmark does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get bookmark", e);
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
            List<Bookmark> bookmarks = BookmarkManager.getInstance().getBookmarks();
            
            APIResult result = APIResultUtils.buildOKAPIResult("Bookmarks have been successfully fetched.");
            result.list = bookmarks;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get bookmarks", e);
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
        	Bookmark bookmark = BookmarkManager.getInstance().getBookmark(id);
            
        	BookmarkManager.getInstance().deleteBookmark(id);
            
            Event event = new Event();
            event.type = EventType.DeleteBookmark;
            event.data.put("data", bookmark);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Bookmark has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete bookmark", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
