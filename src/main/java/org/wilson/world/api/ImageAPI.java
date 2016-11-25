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
import org.wilson.world.image.ImageItem;
import org.wilson.world.image.ImageRef;
import org.wilson.world.image.ImageRefInfo;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.ExpManager;
import org.wilson.world.manager.ImageManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;

@Path("/image")
public class ImageAPI {
    private static final Logger logger = Logger.getLogger(ImageAPI.class);
    
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
                ExpManager.getInstance().train(description, "Gained an extra experience point from training image.");
            }
            
            Event event = new Event();
            event.type = EventType.TrainImage;
            EventManager.getInstance().fireEvent(event);
            
            APIResult result = APIResultUtils.buildOKAPIResult("Image has been successfully trained.");
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to train image", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Image name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(url)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Image url should be provided."));
        }
        url = url.trim();
        
        try {
            ImageManager.getInstance().createImageItem(name, url);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Image has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create image", e);
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
            ImageItem item = ImageManager.getInstance().getImageItem(id);
            if(item != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Image has been successfully fetched.");
                result.data = item;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Image does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get image", e);
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
            List<ImageItem> items = ImageManager.getInstance().getImageItems();
            Collections.sort(items, new Comparator<ImageItem>(){

				@Override
				public int compare(ImageItem o1, ImageItem o2) {
					return Integer.compare(o1.id, o2.id);
				}
            	
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("Images have been successfully fetched.");
            result.list = items;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get images", e);
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
            ImageManager.getInstance().deleteImageItem(id);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Image has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete image", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/list_ref")
    @Produces("application/json")
    public Response listRef(
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
            List<ImageRefInfo> infos = ImageManager.getInstance().getImageRefInfos();
            Collections.sort(infos, new Comparator<ImageRefInfo>(){

				@Override
				public int compare(ImageRefInfo o1, ImageRefInfo o2) {
					return o1.name.compareTo(o2.name);
				}
            	
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("Image ref infos have been successfully fetched.");
            result.list = infos;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get image ref infos", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/get_url")
    @Produces("application/json")
    public Response getUrl(
            @FormParam("name") String name,
            @FormParam("width") int width,
            @FormParam("height") int height,
            @FormParam("adjust") boolean adjust,
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
        	return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Image ref name is needed."));
        }
        
        try {
        	ImageRef ref = ImageManager.getInstance().getImageRef(name);
            if(ref != null) {
            	String url = ref.getUrl(null, width, height, adjust);
            	
                APIResult result = APIResultUtils.buildOKAPIResult("Image url has been successfully fetched.");
                result.data = url;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Image url does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get image url", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/list_ref_ungrouped")
    @Produces("application/json")
    public Response listRefUngrouped(
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
            List<ImageRefInfo> infos = ImageManager.getInstance().getUngroupedImageRefInfos();
            Collections.sort(infos, new Comparator<ImageRefInfo>(){

				@Override
				public int compare(ImageRefInfo o1, ImageRefInfo o2) {
					return o1.name.compareTo(o2.name);
				}
            	
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("Ungrouped image ref infos have been successfully fetched.");
            result.list = infos;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get ungrouped image ref infos", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
