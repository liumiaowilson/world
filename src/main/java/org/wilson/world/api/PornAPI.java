package org.wilson.world.api;

import java.net.URISyntaxException;
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
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.DataManager;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.ExpManager;
import org.wilson.world.manager.InventoryItemManager;
import org.wilson.world.manager.PornManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.porn.PornInfo;
import org.wilson.world.porn.PornItem;

@Path("/porn")
public class PornAPI {
    private static final Logger logger = Logger.getLogger(PornAPI.class);
    
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
            PornInfo info = PornManager.getInstance().randomPorn();
            if(info == null) {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Random porn does not exist."));
            }
            
            PornManager.getInstance().downloadPorn(info);
            
            PornManager.getInstance().removePornInfo(info);
            
            APIResult result = APIResultUtils.buildOKAPIResult("Random porn has been successfully generated.");
            result.data = info;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to generate random porn", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/train")
    @Produces("application/json")
    public Response train(
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
        
        try {
            if(!StringUtils.isBlank(description)) {
                ExpManager.getInstance().train(description, "Gained an extra experience point from training porn.");
            }
            
            Event event = new Event();
            event.type = EventType.TrainPorn;
            EventManager.getInstance().fireEvent(event);
            
            APIResult result = APIResultUtils.buildOKAPIResult("Porn has been successfully trained.");
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to train porn", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/set_source")
    @Produces("application/json")
    public Response setSource(
            @QueryParam("source") String source,
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
            PornManager.getInstance().setSource(source);
            
            APIResult result = APIResultUtils.buildOKAPIResult("Source has been successfully set.");
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to set source", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/save")
    @Produces("application/json")
    public Response save(
            @FormParam("id") int id,
            @FormParam("name") String name,
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
            if(StringUtils.isBlank(name)) {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Porn name should be provided."));
            }
            
            PornInfo info = PornManager.getInstance().getPornInfo(id);
            if(info == null) {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Porn info is not found."));
            }
            
            String ret = PornManager.getInstance().savePornInfo(info, name);
            if(ret == null) {
                Event event = new Event();
                event.type = EventType.SavePorn;
                event.data.put("data", info);
                event.data.put("name", name);
                EventManager.getInstance().fireEvent(event);
                
                APIResult result = APIResultUtils.buildOKAPIResult("Porn has been successfully saved.");
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(ret));
            }
        }
        catch(Exception e) {
            logger.error("failed to save porn", e);
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
            List<PornItem> items = PornManager.getInstance().getPornItems();
            
            APIResult result = APIResultUtils.buildOKAPIResult("Items have been successfully fetched.");
            result.list = items;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get items", e);
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
            PornItem item = PornManager.getInstance().randomPornItem();
            if(item == null) {
                return APIResultUtils.buildURLResponse(request, "public_error.jsp", "No porn item is found");
            }
            
            if(!ConfigManager.getInstance().isInDebugMode()) {
                boolean pass = InventoryItemManager.getInstance().readGalleryTicket();
                if(!pass) {
                    return APIResultUtils.buildURLResponse(request, "public_error.jsp", "No enough gallery ticket to view the porn");
                }
            }
            
            String porn_url = PornManager.getInstance().getImageUrl(item);
            
            request.getSession().setAttribute("world-public-porn", porn_url);
            
            return APIResultUtils.buildURLResponse(request, "public/view_porn.jsp");
        }
        catch(Exception e) {
            logger.error("failed to view porn", e);
            return APIResultUtils.buildURLResponse(request, "public_error.jsp", e.getMessage());
        }
    }
    
    @GET
    @Path("/clean")
    @Produces("application/json")
    public Response clean(
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
            PornManager.getInstance().getPornsRemoved().clear();
            APIResult result = APIResultUtils.buildOKAPIResult("Removed porns have been successfully cleaned.");
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to clean removed porns!", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
