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
import org.wilson.world.manager.NovelTicketManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.NovelTicket;

@Path("novel_ticket")
public class NovelTicketAPI {
    private static final Logger logger = Logger.getLogger(NovelTicketAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
    		@FormParam("docId") String docId,
            @FormParam("name") String name, 
            @FormParam("description") String description,
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
        
        if(StringUtils.isBlank(docId)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("NovelTicket docId should be provided."));
        }
        docId = docId.trim();
        if(StringUtils.isBlank(name)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("NovelTicket name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("NovelTicket description should be provided."));
        }
        description = description.trim();
        
        try {
        	NovelTicket ticket = new NovelTicket();
        	ticket.docId = docId;
            ticket.name = name;
            ticket.description = description;
            NovelTicketManager.getInstance().createNovelTicket(ticket);
            
            Event event = new Event();
            event.type = EventType.CreateNovelTicket;
            event.data.put("data", ticket);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("NovelTicket has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create novel ticket", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("docId") String docId,
            @FormParam("name") String name, 
            @FormParam("description") String description,
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
        
        if(StringUtils.isBlank(docId)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("NovelTicket docId should be provided."));
        }
        docId = docId.trim();
        if(StringUtils.isBlank(name)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("NovelTicket name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("NovelTicket description should be provided."));
        }
        description = description.trim();
        
        try {
        	NovelTicket oldTicket = NovelTicketManager.getInstance().getNovelTicket(id);
            
        	NovelTicket ticket = new NovelTicket();
            ticket.id = id;
            ticket.docId = docId;
            ticket.name = name;
            ticket.description = description;
            NovelTicketManager.getInstance().updateNovelTicket(ticket);
            
            Event event = new Event();
            event.type = EventType.UpdateNovelTicket;
            event.data.put("old_data", oldTicket);
            event.data.put("new_data", ticket);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("NovelTicket has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update novel ticket", e);
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
        	NovelTicket ticket = NovelTicketManager.getInstance().getNovelTicket(id);
            if(ticket != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("NovelTicket has been successfully fetched.");
                result.data = ticket;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("NovelTicket does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get novel ticket", e);
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
            List<NovelTicket> tickets = NovelTicketManager.getInstance().getNovelTickets();
            
            Collections.sort(tickets, new Comparator<NovelTicket>(){

				@Override
				public int compare(NovelTicket o1, NovelTicket o2) {
					return Integer.compare(o1.id, o2.id);
				}
            	
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("NovelTickets have been successfully fetched.");
            result.list = tickets;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get novel tickets", e);
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
        	NovelTicket ticket = NovelTicketManager.getInstance().getNovelTicket(id);
            
        	NovelTicketManager.getInstance().deleteNovelTicket(id);
            
            Event event = new Event();
            event.type = EventType.DeleteNovelTicket;
            event.data.put("data", ticket);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("NovelTicket has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete novel ticket", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/resolve")
    @Produces("application/json")
    public Response resolve(
            @QueryParam("docId") String docId,
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
        
        if(StringUtils.isBlank(docId)) {
        	return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Novel Document ID is needed."));
        }
        
        try {
        	NovelTicket ticket = NovelTicketManager.getInstance().getNovelTicket(docId);
            if(ticket != null) {
            	NovelTicketManager.getInstance().deleteNovelTicket(ticket.id);
                
                Event event = new Event();
                event.type = EventType.DeleteNovelTicket;
                event.data.put("data", ticket);
                EventManager.getInstance().fireEvent(event);
            	
                APIResult result = APIResultUtils.buildOKAPIResult("NovelTicket has been successfully resolved.");
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("NovelTicket does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to resolve novel ticket", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
