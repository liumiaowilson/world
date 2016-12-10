package org.wilson.world.api;

import java.net.URISyntaxException;
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
import org.wilson.world.image.DefaultImageContributor;
import org.wilson.world.image.ImageContributor;
import org.wilson.world.image.ImageListInfo;
import org.wilson.world.image.ImageRef;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.DataManager;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.ImageListManager;
import org.wilson.world.manager.ImageManager;
import org.wilson.world.manager.InventoryItemManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.manager.StorageManager;
import org.wilson.world.manager.ThreadPoolManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.ImageList;

@Path("image_list")
public class ImageListAPI {
    private static final Logger logger = Logger.getLogger(ImageListAPI.class);
    
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("ImageList name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("ImageList content should be provided."));
        }
        content = content.trim();
        
        try {
        	ImageList list = new ImageList();
            list.name = name;
            list.content = content;
            ImageListManager.getInstance().createImageList(list);
            
            Event event = new Event();
            event.type = EventType.CreateImageList;
            event.data.put("data", list);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("ImageList has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create image list", e);
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("ImageList name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("ImageList content should be provided."));
        }
        content = content.trim();
        
        try {
        	ImageList oldList = ImageListManager.getInstance().getImageList(id);
            
        	ImageList list = new ImageList();
            list.id = id;
            list.name = name;
            list.content = content;
            ImageListManager.getInstance().updateImageList(list);
            
            Event event = new Event();
            event.type = EventType.UpdateImageList;
            event.data.put("old_data", oldList);
            event.data.put("new_data", list);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("ImageList has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update image list", e);
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
        	ImageList list = ImageListManager.getInstance().getImageList(id);
            if(list != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("ImageList has been successfully fetched.");
                result.data = list;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("ImageList does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get image list", e);
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
            List<ImageList> lists = ImageListManager.getInstance().getImageLists();
            Collections.sort(lists, new Comparator<ImageList>(){

				@Override
				public int compare(ImageList o1, ImageList o2) {
					return Integer.compare(o1.id, o2.id);
				}
            	
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("ImageLists have been successfully fetched.");
            result.list = lists;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get image lists", e);
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
        	ImageList list = ImageListManager.getInstance().getImageList(id);
            
        	ImageListManager.getInstance().deleteImageList(id);
            
            Event event = new Event();
            event.type = EventType.DeleteImageList;
            event.data.put("data", list);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("ImageList has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete image list", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/list_info")
    @Produces("application/json")
    public Response listInfo(
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
            List<ImageListInfo> infos = ImageListManager.getInstance().getImageListInfos();
            Collections.sort(infos, new Comparator<ImageListInfo>(){

				@Override
				public int compare(ImageListInfo o1, ImageListInfo o2) {
					return -Integer.compare(o1.count, o2.count);
				}
            	
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("ImageListInfos have been successfully fetched.");
            result.list = infos;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get image list infos", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/sort")
    @Produces("application/json")
    public Response sort(
    		@FormParam("id") int id,
            @FormParam("names") String names, 
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Names should be provided."));
        }
        names = names.trim();
        
        try {
        	ImageList oldList = ImageListManager.getInstance().getImageList(id);
        	
        	List<String> refs = new ArrayList<String>();
        	for(String name : names.split(",")) {
        		name = name.trim();
        		refs.add(name);
        	}
        	
        	ImageList list = new ImageList();
            list.id = id;
            list.name = oldList.name;
            list.content = ImageListManager.getInstance().toImageListContent(refs);
            ImageListManager.getInstance().updateImageList(list);
            
            Event event = new Event();
            event.type = EventType.UpdateImageList;
            event.data.put("old_data", oldList);
            event.data.put("new_data", list);
            EventManager.getInstance().fireEvent(event);
            
            //handle deleted
            final List<String> deletedRefs = new ArrayList<String>();
            for(String ref : oldList.refs) {
            	if(!refs.contains(ref)) {
            		//deleted
            		deletedRefs.add(ref);
            	}
            }
            ThreadPoolManager.getInstance().execute(new Runnable(){

				@Override
				public void run() {
					for(String ref : deletedRefs) {
						ImageRef imageRef = ImageManager.getInstance().getImageRef(ref);
						ImageManager.getInstance().deleteImageRef(imageRef);
					}
				}
            	
            });
        	
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("ImageList has been successfully sorted."));
        }
        catch(Exception e) {
            logger.error("failed to sort image list", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/batch")
    @Produces("application/json")
    public Response batch(
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("ImageList name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("ImageList content should be provided."));
        }
        content = content.trim();
        
        try {
        	final List<String> urls = new ArrayList<String>();
        	String [] items = content.split("\n");
        	for(String item : items) {
        		urls.add(item.trim());
        	}
        	
        	if(urls.size() >= 1000) {
        		return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("ImageList is over-sized."));
        	}
        	
        	final String [] valueHolder = new String [1];
        	valueHolder[0] = name;
        	
        	ThreadPoolManager.getInstance().execute(new Runnable(){

				@Override
				public void run() {
		        	ImageList list = new ImageList();
		            list.name = valueHolder[0];
		            
		            for(int i = 0; i < urls.size(); i++) {
		            	String url = urls.get(i);
		            	String name = list.name + "/" + StringUtils.leftPad(String.valueOf(i), 3, '0');
		            	String path = ImageManager.STORAGE_PREFIX + name + ImageManager.STORAGE_SUFFIX;
		            	try {
		            		StorageManager.getInstance().createStorageAsset(path, url);
		            		logger.info("Downloaded " + path + " from " + url);
		            		
		            		list.refs.add(DefaultImageContributor.IMAGE_PREFIX + ImageContributor.PREFIX_SEPARATOR + name);
		            	}
		            	catch(Exception e) {
		            		logger.warn("Failed to download " + path + " from " + url);
		            	}
		            }
		            
		            list.content = ImageListManager.getInstance().toImageListContent(list.refs);
		            ImageListManager.getInstance().createImageList(list);
		            
		            Event event = new Event();
		            event.type = EventType.CreateImageList;
		            event.data.put("data", list);
		            EventManager.getInstance().fireEvent(event);
				}
        		
        	});
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("ImageList has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to batch create image list", e);
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
            ImageList image_list = ImageListManager.getInstance().randomImageList();
            if(image_list == null) {
                return APIResultUtils.buildURLResponse(request, "public_error.jsp", "No image list is found");
            }
            
            if(!ConfigManager.getInstance().isInDebugMode()) {
                boolean pass = InventoryItemManager.getInstance().readGalleryTicket();
                if(!pass) {
                    return APIResultUtils.buildURLResponse(request, "public_error.jsp", "No enough gallery ticket to view the image list");
                }
            }
            
            request.getSession().setAttribute("world-public-image_list", image_list);
            
            return APIResultUtils.buildURLResponse(request, "public/view_image_list.jsp");
        }
        catch(Exception e) {
            logger.error("failed to view image list", e);
            return APIResultUtils.buildURLResponse(request, "public_error.jsp", e.getMessage());
        }
    }
}
