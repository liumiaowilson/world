package org.wilson.world.api;

import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
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
import org.wilson.world.manager.NotesManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;

@Path("/notes")
public class NotesAPI {
    private static final Logger logger = Logger.getLogger(NotesAPI.class);
    
    @POST
    @Path("/set_notes")
    @Produces("application/json")
    public Response setNotes(
            @FormParam("notes") String notes,
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
            NotesManager.getInstance().setNotes(notes);
            APIResult result = APIResultUtils.buildOKAPIResult("Notes have been successfully saved.");
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to set notes", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/send_notes")
    @Produces("application/json")
    public Response sendNotes(
            @FormParam("notes") String notes,
            @Context HttpHeaders headers,
            @Context HttpServletRequest request,
            @Context UriInfo uriInfo) throws URISyntaxException {
        NotesManager.getInstance().setNotes(notes);
        
        return APIResultUtils.buildURLResponse(request, "public/notes.jsp");
    }
}
