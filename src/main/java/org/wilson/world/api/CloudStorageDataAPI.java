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
import org.wilson.world.manager.CloudStorageDataManager;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.CloudStorageData;

@Path("cloud_storage_data")
public class CloudStorageDataAPI {
    private static final Logger logger = Logger.getLogger(CloudStorageDataAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("service") String service, 
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("CloudStorageData name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(service)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("CloudStorageData service should be provided."));
        }
        service = service.trim();
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("CloudStorageData content should be provided."));
        }
        content = content.trim();
        
        try {
        	CloudStorageData data = new CloudStorageData();
            data.name = name;
            data.service = service;
            data.content = content;
            CloudStorageDataManager.getInstance().createCloudStorageData(data);
            
            Event event = new Event();
            event.type = EventType.CreateCloudStorageData;
            event.data.put("data", data);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("CloudStorageData has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create cloud storage data", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("name") String name, 
            @FormParam("service") String service, 
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("CloudStorageData name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(service)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("CloudStorageData service should be provided."));
        }
        service = service.trim();
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("CloudStorageData content should be provided."));
        }
        content = content.trim();
        
        try {
        	CloudStorageData oldData = CloudStorageDataManager.getInstance().getCloudStorageData(id);
            
        	CloudStorageData data = new CloudStorageData();
            data.id = id;
            data.name = name;
            data.service = service;
            data.content = content;
            CloudStorageDataManager.getInstance().updateCloudStorageData(data);
            
            Event event = new Event();
            event.type = EventType.UpdateCloudStorageData;
            event.data.put("old_data", oldData);
            event.data.put("new_data", data);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("CloudStorageData has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update cloud storage data", e);
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
        	CloudStorageData data = CloudStorageDataManager.getInstance().getCloudStorageData(id);
            if(data != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("CloudStorageData has been successfully fetched.");
                result.data = data;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("CloudStorageData does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get cloud storage data", e);
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
            List<CloudStorageData> datas = CloudStorageDataManager.getInstance().getCloudStorageDatas();
            Collections.sort(datas, new Comparator<CloudStorageData>(){

				@Override
				public int compare(CloudStorageData o1, CloudStorageData o2) {
					return Integer.compare(o1.id, o2.id);
				}
            	
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("CloudStorageDatas have been successfully fetched.");
            result.list = datas;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get cloud storage datas", e);
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
        	CloudStorageData data = CloudStorageDataManager.getInstance().getCloudStorageData(id);
            
        	CloudStorageDataManager.getInstance().deleteCloudStorageData(id);
            
            Event event = new Event();
            event.type = EventType.DeleteCloudStorageData;
            event.data.put("data", data);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("CloudStorageData has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete cloud storage data", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
