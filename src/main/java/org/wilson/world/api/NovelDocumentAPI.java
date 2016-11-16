package org.wilson.world.api;

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
}
