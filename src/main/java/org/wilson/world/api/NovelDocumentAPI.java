package org.wilson.world.api;

import java.net.URISyntaxException;

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
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.DataManager;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.InventoryItemManager;
import org.wilson.world.manager.NovelDocumentManager;
import org.wilson.world.manager.NovelTicketManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.NovelDocument;
import org.wilson.world.model.NovelTicket;
import org.wilson.world.novel.NovelDocumentContent;

@Path("novel_document")
public class NovelDocumentAPI {
    private static final Logger logger = Logger.getLogger(NovelDocumentAPI.class);
    
    @GET
    @Path("/random")
    @Produces("application/json")
    public Response random(
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
        	NovelDocument doc = NovelDocumentManager.getInstance().generateNovelDocument();
        	if(doc != null) {
        		if(!ConfigManager.getInstance().isInDebugMode()) {
                    boolean pass = InventoryItemManager.getInstance().readGalleryTicket();
                    if(!pass) {
                    	return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("No enough gallery ticket to view the novel document."));
                    }
                }
        		
        		String html = NovelDocumentManager.getInstance().toHtml(doc);
        		APIResult result = APIResultUtils.buildOKAPIResult("Novel document has been successfully generated.");
        		NovelDocumentContent content = new NovelDocumentContent();
        		content.id = doc.id;
        		content.content = html;
                result.data = content;
                return APIResultUtils.buildJSONResponse(result);
        	}
        	else {
        		return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Novel document does not exist."));
        	}
        }
        catch(Exception e) {
            logger.error("failed to get random novel document", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/view_public")
    @Produces("application/json")
    public Response viewPublic(
    		@FormParam("docId") String docId,
    		@FormParam("comment") String comment,
    		@FormParam("showImage") boolean showImage,
            @FormParam("key") String key,
            @Context HttpHeaders headers,
            @Context HttpServletRequest request,
            @Context UriInfo uriInfo) throws URISyntaxException {
        String k = DataManager.getInstance().getValue("public.key");
        if(k == null || !k.equals(key)) {
            return APIResultUtils.buildURLResponse(request, "public_error.jsp");
        }
        
        try {
        	if(StringUtils.isNotBlank(docId) && StringUtils.isNotBlank(comment)) {
        		String name = comment;
                if(name.length() > 20) {
                	name = comment.substring(0, 20);
                }
        		
        		NovelTicket ticket = new NovelTicket();
            	ticket.docId = docId;
                ticket.name = name;
                ticket.description = comment;
                NovelTicketManager.getInstance().createNovelTicket(ticket);
                
                Event event = new Event();
                event.type = EventType.CreateNovelTicket;
                event.data.put("data", ticket);
                EventManager.getInstance().fireEvent(event);
        	}
        	
        	NovelDocument doc = NovelDocumentManager.getInstance().generateNovelDocument();
            if(doc == null) {
                return APIResultUtils.buildURLResponse(request, "public_error.jsp", "No novel document is found");
            }
            
            if(!ConfigManager.getInstance().isInDebugMode()) {
                boolean pass = InventoryItemManager.getInstance().readGalleryTicket();
                if(!pass) {
                    return APIResultUtils.buildURLResponse(request, "public_error.jsp", "No enough gallery ticket to view the novel document.");
                }
            }
            
            String html = NovelDocumentManager.getInstance().toHtml(doc, false, showImage);
            
            request.getSession().setAttribute("world-public-novel_document", html);
            request.getSession().setAttribute("world-public-novel_document_id", doc.id);
            
            return APIResultUtils.buildURLResponse(request, "public/view_novel_document.jsp");
        }
        catch(Exception e) {
            logger.error("failed to view novel document", e);
            return APIResultUtils.buildURLResponse(request, "public_error.jsp", e.getMessage());
        }
    }
    
    @GET
    @Path("/review")
    @Produces("application/json")
    public Response review(
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
        
        if(StringUtils.isBlank(id)) {
        	return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Novel document ID is needed."));
        }
        
        try {
        	NovelDocument doc = NovelDocumentManager.getInstance().getNovelDocument(id);
        	if(doc != null) {
        		String html = NovelDocumentManager.getInstance().toHtml(doc, true);
        		APIResult result = APIResultUtils.buildOKAPIResult("Novel document has been successfully fetched.");
                result.data = html;
                return APIResultUtils.buildJSONResponse(result);
        	}
        	else {
        		return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Novel document does not exist."));
        	}
        }
        catch(Exception e) {
            logger.error("failed to get novel document", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/comment")
    @Produces("application/json")
    public Response comment(
    		@FormParam("docId") String docId,
            @FormParam("comment") String comment,
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
        if(StringUtils.isBlank(comment)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("NovelTicket description should be provided."));
        }
        comment = comment.trim();
        String name = comment;
        if(name.length() > 20) {
        	name = comment.substring(0, 20);
        }
        
        try {
        	NovelTicket ticket = new NovelTicket();
        	ticket.docId = docId;
            ticket.name = name;
            ticket.description = comment;
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
}
