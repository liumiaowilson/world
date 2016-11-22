package org.wilson.world.api;

import java.util.ArrayList;
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
import org.wilson.world.image.ImageRef;
import org.wilson.world.image.ImageSetImageInfo;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.ImageManager;
import org.wilson.world.manager.ImageSetManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.ImageSet;

@Path("image_set")
public class ImageSetAPI {
    private static final Logger logger = Logger.getLogger(ImageSetAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("content") String content,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("ImageSet name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("ImageSet content should be provided."));
        }
        content = content.trim();
        
        try {
        	ImageSet set = new ImageSet();
            set.name = name;
            set.content = content;
            ImageSetManager.getInstance().createImageSet(set);
            
            Event event = new Event();
            event.type = EventType.CreateImageSet;
            event.data.put("data", set);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("ImageSet has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create image set", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("name") String name, 
            @FormParam("content") String content,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("ImageSet name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("ImageSet content should be provided."));
        }
        content = content.trim();
        
        try {
        	ImageSet oldSet = ImageSetManager.getInstance().getImageSet(id);
            
        	ImageSet set = new ImageSet();
            set.id = id;
            set.name = name;
            set.content = content;
            ImageSetManager.getInstance().updateImageSet(set);
            
            Event event = new Event();
            event.type = EventType.UpdateImageSet;
            event.data.put("old_data", oldSet);
            event.data.put("new_data", set);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("ImageSet has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update image set", e);
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
        	ImageSet set = ImageSetManager.getInstance().getImageSet(id);
            if(set != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("ImageSet has been successfully fetched.");
                result.data = set;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("ImageSet does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get image set", e);
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
            List<ImageSet> sets = ImageSetManager.getInstance().getImageSets();
            Collections.sort(sets, new Comparator<ImageSet>(){

				@Override
				public int compare(ImageSet o1, ImageSet o2) {
					return Integer.compare(o1.id, o2.id);
				}
            	
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("ImageSets have been successfully fetched.");
            result.list = sets;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get image sets", e);
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
        	ImageSet set = ImageSetManager.getInstance().getImageSet(id);
            
        	ImageSetManager.getInstance().deleteImageSet(id);
            
            Event event = new Event();
            event.type = EventType.DeleteImageSet;
            event.data.put("data", set);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("ImageSet has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete image set", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/set_image_sets")
    @Produces("application/json")
    public Response setImageSets(
            @FormParam("names") String names, 
            @FormParam("image") String image,
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
        
        if(StringUtils.isBlank(names)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("ImageSet names should be provided."));
        }
        names = names.trim();
        if(StringUtils.isBlank(image)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("ImageSet image should be provided."));
        }
        image = image.trim();
        
        try {
        	List<ImageSet> oldSets = ImageSetManager.getInstance().getEnclosingImageSets(image);
        	List<String> nameList = new ArrayList<String>();
        	for(String name : names.split(",")) {
        		nameList.add(name);
        	}
        	
        	List<ImageSet> addedSets = new ArrayList<ImageSet>();
        	List<ImageSet> removedSets = new ArrayList<ImageSet>();
        	
        	for(ImageSet set : oldSets) {
        		if(!nameList.contains(set.name)) {
        			removedSets.add(set);
        		}
        		else {
        			nameList.remove(set.name);
        		}
        	}
        	for(String name : nameList) {
        		ImageSet set = ImageSetManager.getInstance().getImageSet(name);
        		if(set != null) {
        			addedSets.add(set);
        		}
        	}
        	
        	for(ImageSet oldSet : addedSets) {
            	ImageSet set = new ImageSet();
                set.id = oldSet.id;
                set.name = oldSet.name;
                List<String> refs = new ArrayList<String>(oldSet.refs);
                if(!refs.contains(image)) {
                	refs.add(image);
                }
                set.content = ImageSetManager.getInstance().toImageSetContent(refs);
                ImageSetManager.getInstance().updateImageSet(set);
                
                Event event = new Event();
                event.type = EventType.UpdateImageSet;
                event.data.put("old_data", oldSet);
                event.data.put("new_data", set);
                EventManager.getInstance().fireEvent(event);
        	}
        	
        	for(ImageSet oldSet : removedSets) {
            	ImageSet set = new ImageSet();
                set.id = oldSet.id;
                set.name = oldSet.name;
                List<String> refs = new ArrayList<String>(oldSet.refs);
                refs.remove(image);
                set.content = ImageSetManager.getInstance().toImageSetContent(refs);
                ImageSetManager.getInstance().updateImageSet(set);
                
                Event event = new Event();
                event.type = EventType.UpdateImageSet;
                event.data.put("old_data", oldSet);
                event.data.put("new_data", set);
                EventManager.getInstance().fireEvent(event);
        	}
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("ImageSets have been successfully update."));
        }
        catch(Exception e) {
            logger.error("failed to update image sets", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/get_image")
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
            	ref.setWidth(width);
            	ref.setHeight(height);
            	ref.setAdjust(adjust);
            	ImageSetImageInfo info = new ImageSetImageInfo();
            	info.url = ref.getUrl();
            	List<ImageSet> sets = ImageSetManager.getInstance().getEnclosingImageSets(name);
            	StringBuilder sb = new StringBuilder();
            	for(int i = 0; i < sets.size(); i++) {
            		sb.append(sets.get(i).name);
            		if(i != sets.size() - 1) {
            			sb.append(",");
            		}
            	}
            	info.setNames = sb.toString();
            	
                APIResult result = APIResultUtils.buildOKAPIResult("Image set image info has been successfully fetched.");
                result.data = info;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Image set image info does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get image url", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
