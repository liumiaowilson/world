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
import org.wilson.world.manager.NovelStageManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.NovelStage;

@Path("novel_stage")
public class NovelStageAPI {
    private static final Logger logger = Logger.getLogger(NovelStageAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("description") String description,
            @FormParam("previousId") int previousId,
            @FormParam("status") String status,
            @FormParam("image") String image,
            @FormParam("condition") String condition,
            @FormParam("preCode") String preCode,
            @FormParam("postCode") String postCode,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("NovelStage name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("NovelStage description should be provided."));
        }
        description = description.trim();
        
        try {
        	NovelStage stage = new NovelStage();
            stage.name = name;
            stage.description = description;
            stage.previousId = previousId;
            stage.status = status;
            stage.image = image;
            stage.condition = condition;
            stage.preCode = preCode;
            stage.postCode = postCode;
            NovelStageManager.getInstance().createNovelStage(stage);
            
            Event event = new Event();
            event.type = EventType.CreateNovelStage;
            event.data.put("data", stage);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("NovelStage has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create novel stage", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("name") String name, 
            @FormParam("description") String description,
            @FormParam("previousId") int previousId,
            @FormParam("status") String status,
            @FormParam("image") String image,
            @FormParam("condition") String condition,
            @FormParam("preCode") String preCode,
            @FormParam("postCode") String postCode,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("NovelStage name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("NovelStage description should be provided."));
        }
        description = description.trim();
        
        try {
        	NovelStage oldStage = NovelStageManager.getInstance().getNovelStage(id);
            
        	NovelStage stage = new NovelStage();
            stage.id = id;
            stage.name = name;
            stage.description = description;
            stage.previousId = previousId;
            stage.status = status;
            stage.image = image;
            stage.condition = condition;
            stage.preCode = preCode;
            stage.postCode = postCode;
            NovelStageManager.getInstance().updateNovelStage(stage);
            
            Event event = new Event();
            event.type = EventType.UpdateNovelStage;
            event.data.put("old_data", oldStage);
            event.data.put("new_data", stage);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("NovelStage has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update novel stage", e);
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
        	NovelStage stage = NovelStageManager.getInstance().getNovelStage(id);
            if(stage != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("NovelStage has been successfully fetched.");
                result.data = stage;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("NovelStage does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get novel stage", e);
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
            List<NovelStage> stages = NovelStageManager.getInstance().getNovelStages();
            
            Collections.sort(stages, new Comparator<NovelStage>(){

				@Override
				public int compare(NovelStage o1, NovelStage o2) {
					return Integer.compare(o1.id, o2.id);
				}
            	
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("NovelStages have been successfully fetched.");
            result.list = stages;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get novel stages", e);
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
        	NovelStage stage = NovelStageManager.getInstance().getNovelStage(id);
            
        	NovelStageManager.getInstance().deleteNovelStage(id);
            
            Event event = new Event();
            event.type = EventType.DeleteNovelStage;
            event.data.put("data", stage);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("NovelStage has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete novel stage", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
