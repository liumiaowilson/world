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
import org.wilson.world.manager.PageletManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.Pagelet;

@Path("pagelet")
public class PageletAPI {
    private static final Logger logger = Logger.getLogger(PageletAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("target") String target,
            @FormParam("serverCode") String serverCode,
            @FormParam("css") String css,
            @FormParam("html") String html,
            @FormParam("clientCode") String clientCode,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Pagelet name should be provided."));
        }
        name = name.trim();
        
        try {
        	Pagelet pagelet = new Pagelet();
            pagelet.name = name;
            pagelet.target = target;
            pagelet.serverCode = serverCode;
            pagelet.css = css;
            pagelet.html = html;
            pagelet.clientCode = clientCode;
            PageletManager.getInstance().createPagelet(pagelet);
            
            Event event = new Event();
            event.type = EventType.CreatePagelet;
            event.data.put("data", pagelet);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Pagelet has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create pagelet", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("name") String name, 
            @FormParam("target") String target,
            @FormParam("serverCode") String serverCode,
            @FormParam("css") String css,
            @FormParam("html") String html,
            @FormParam("clientCode") String clientCode,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Pagelet name should be provided."));
        }
        name = name.trim();
        
        try {
        	Pagelet oldPagelet = PageletManager.getInstance().getPagelet(id);
            
        	Pagelet pagelet = new Pagelet();
            pagelet.id = id;
            pagelet.name = name;
            pagelet.target = target;
            pagelet.serverCode = serverCode;
            pagelet.css = css;
            pagelet.html = html;
            pagelet.clientCode = clientCode;
            PageletManager.getInstance().updatePagelet(pagelet);
            
            Event event = new Event();
            event.type = EventType.UpdatePagelet;
            event.data.put("old_data", oldPagelet);
            event.data.put("new_data", pagelet);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Pagelet has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update pagelet", e);
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
        	Pagelet pagelet = PageletManager.getInstance().getPagelet(id, false);
            if(pagelet != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Pagelet has been successfully fetched.");
                result.data = pagelet;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Pagelet does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get pagelet", e);
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
            List<Pagelet> pagelets = PageletManager.getInstance().getPagelets();
            Collections.sort(pagelets, new Comparator<Pagelet>() {

				@Override
				public int compare(Pagelet o1, Pagelet o2) {
					return Integer.compare(o1.id, o2.id);
				}
            	
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("Pagelets have been successfully fetched.");
            result.list = pagelets;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get pagelets", e);
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
        	Pagelet pagelet = PageletManager.getInstance().getPagelet(id);
            
        	PageletManager.getInstance().deletePagelet(id);
            
            Event event = new Event();
            event.type = EventType.DeletePagelet;
            event.data.put("data", pagelet);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Pagelet has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete pagelet", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
