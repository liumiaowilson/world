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
import org.wilson.world.manager.SecManager;
import org.wilson.world.manager.StorageManager;
import org.wilson.world.manager.WebManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.storage.StorageAsset;
import org.wilson.world.storage.StorageSyncJob;
import org.wilson.world.web.WebJob;

@Path("storage_asset")
public class StorageAssetAPI {
    private static final Logger logger = Logger.getLogger(StorageAssetAPI.class);
    
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Storage asset name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(url)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Storage asset url should be provided."));
        }
        url = url.trim();
        
        try {
            String ret = StorageManager.getInstance().createStorageAsset(name, url);
            if(ret == null) {
                Event event = new Event();
                event.type = EventType.CreateStorageAsset;
                StorageAsset asset = StorageManager.getInstance().getStorageAsset(name);
                event.data.put("data", asset);
                EventManager.getInstance().fireEvent(event);
                
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Storage asset has been successfully created."));
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(ret));
            }
        }
        catch(Exception e) {
            logger.error("failed to create asset", e);
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
            List<StorageAsset> assets = StorageManager.getInstance().getStorageAssets();
            Collections.sort(assets, new Comparator<StorageAsset>(){

				@Override
				public int compare(StorageAsset o1, StorageAsset o2) {
					return Integer.compare(o1.id, o2.id);
				}
            	
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("Assets have been successfully fetched.");
            result.list = assets;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get assets", e);
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
            StorageAsset asset = StorageManager.getInstance().getStorageAsset(id);
            
            if(asset == null) {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Asset does not exist"));
            }
            
            String ret = StorageManager.getInstance().deleteStorageAsset(asset.name);
            if(ret == null) {
                Event event = new Event();
                event.type = EventType.DeleteStorageAsset;
                event.data.put("data", asset);
                EventManager.getInstance().fireEvent(event);
                
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Storage asset has been successfully deleted."));
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(ret));
            }
        }
        catch(Exception e) {
            logger.error("failed to delete asset", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/sync")
    @Produces("application/json")
    public Response sync(
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
            String jobName = StorageSyncJob.class.getSimpleName();
            WebJob job = WebManager.getInstance().getAvailableWebJobByName(jobName);
            if(job == null) {
            	return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Failed to sync because of no available job."));
            }
            
            WebManager.getInstance().run(job);
            
            APIResult result = APIResultUtils.buildOKAPIResult("Sync job has been successfully started.");
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to sync assets", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
