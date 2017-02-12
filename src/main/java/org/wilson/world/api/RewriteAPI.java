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
import org.wilson.world.manager.RewriteManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.Rewrite;

@Path("rewrite")
public class RewriteAPI {
    private static final Logger logger = Logger.getLogger(RewriteAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("regex") String regex,
            @FormParam("fromUrl") String fromUrl,
            @FormParam("toUrl") String toUrl,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Rewrite name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(regex)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Rewrite regex should be provided."));
        }
        regex = regex.trim();
        if(StringUtils.isBlank(fromUrl)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Rewrite fromUrl should be provided."));
        }
        fromUrl = fromUrl.trim();
        if(StringUtils.isBlank(toUrl)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Rewrite toUrl should be provided."));
        }
        toUrl = toUrl.trim();
        
        try {
        	Rewrite rewrite = new Rewrite();
            rewrite.name = name;
            rewrite.regex = regex;
            rewrite.fromUrl = fromUrl;
            rewrite.toUrl = toUrl;
            RewriteManager.getInstance().createRewrite(rewrite);
            
            Event event = new Event();
            event.type = EventType.CreateRewrite;
            event.data.put("data", rewrite);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Rewrite has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create rewrite", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("name") String name, 
            @FormParam("regex") String regex,
            @FormParam("fromUrl") String fromUrl,
            @FormParam("toUrl") String toUrl,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Rewrite name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(regex)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Rewrite regex should be provided."));
        }
        regex = regex.trim();
        if(StringUtils.isBlank(fromUrl)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Rewrite fromUrl should be provided."));
        }
        fromUrl = fromUrl.trim();
        if(StringUtils.isBlank(toUrl)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Rewrite toUrl should be provided."));
        }
        toUrl = toUrl.trim();
        
        try {
        	Rewrite oldRewrite = RewriteManager.getInstance().getRewrite(id);
            
        	Rewrite rewrite = new Rewrite();
            rewrite.id = id;
            rewrite.name = name;
            rewrite.regex = regex;
            rewrite.fromUrl = fromUrl;
            rewrite.toUrl = toUrl;
            RewriteManager.getInstance().updateRewrite(rewrite);
            
            Event event = new Event();
            event.type = EventType.UpdateRewrite;
            event.data.put("old_data", oldRewrite);
            event.data.put("new_data", rewrite);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Rewrite has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update rewrite", e);
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
        	Rewrite rewrite = RewriteManager.getInstance().getRewrite(id);
            if(rewrite != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Rewrite has been successfully fetched.");
                result.data = rewrite;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Rewrite does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get rewrite", e);
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
            List<Rewrite> rewrites = RewriteManager.getInstance().getRewrites();
            Collections.sort(rewrites, new Comparator<Rewrite>() {

				@Override
				public int compare(Rewrite o1, Rewrite o2) {
					return Integer.compare(o1.id, o2.id);
				}
            	
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("Rewrites have been successfully fetched.");
            result.list = rewrites;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get rewrites", e);
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
        	Rewrite rewrite = RewriteManager.getInstance().getRewrite(id);
            
        	RewriteManager.getInstance().deleteRewrite(id);
            
            Event event = new Event();
            event.type = EventType.DeleteRewrite;
            event.data.put("data", rewrite);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Rewrite has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete rewrite", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
