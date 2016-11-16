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
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.DataManager;
import org.wilson.world.manager.InventoryItemManager;
import org.wilson.world.manager.NovelDocumentManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.NovelDocument;

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
        		
        		String html = NovelDocumentManager.getInstance().toString(doc);
        		html = html.replaceAll("\n", "<br/>");
        		APIResult result = APIResultUtils.buildOKAPIResult("Novel document has been successfully generated.");
                result.data = html;
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
            @FormParam("key") String key,
            @Context HttpHeaders headers,
            @Context HttpServletRequest request,
            @Context UriInfo uriInfo) throws URISyntaxException {
        String k = DataManager.getInstance().getValue("public.key");
        if(k == null || !k.equals(key)) {
            return APIResultUtils.buildURLResponse(request, "public_error.jsp");
        }
        
        try {
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
            
            String html = NovelDocumentManager.getInstance().toString(doc);
    		String content = html.replaceAll("\n", "<br/>");
            
            request.getSession().setAttribute("world-public-novel_document", content);
            
            return APIResultUtils.buildURLResponse(request, "public/view_novel_document.jsp");
        }
        catch(Exception e) {
            logger.error("failed to view novel document", e);
            return APIResultUtils.buildURLResponse(request, "public_error.jsp", e.getMessage());
        }
    }
}
