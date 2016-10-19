package org.wilson.world.api;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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
import org.wilson.world.behavior.BehaviorInfo;
import org.wilson.world.event.Event;
import org.wilson.world.event.EventType;
import org.wilson.world.manager.BehaviorManager;
import org.wilson.world.manager.DataManager;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.Behavior;
import org.wilson.world.util.TimeUtils;

@Path("behavior")
public class BehaviorAPI {
    private static final Logger logger = Logger.getLogger(BehaviorAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("defId") int defId,
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
            Behavior behavior = new Behavior();
            behavior.defId = defId;
            behavior.time = System.currentTimeMillis();
            BehaviorManager.getInstance().createBehavior(behavior);
            
            Event event = new Event();
            event.type = EventType.CreateBehavior;
            event.data.put("data", behavior);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Behavior has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create behavior", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("defId") int defId, 
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
            Behavior oldBehavior = BehaviorManager.getInstance().getBehavior(id);
            
            Behavior behavior = new Behavior();
            behavior.id = id;
            behavior.defId = defId;
            behavior.time = System.currentTimeMillis();
            BehaviorManager.getInstance().updateBehavior(behavior);
            
            Event event = new Event();
            event.type = EventType.UpdateBehavior;
            event.data.put("old_data", oldBehavior);
            event.data.put("new_data", behavior);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Behavior has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update behavior", e);
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
            Behavior behavior = BehaviorManager.getInstance().getBehavior(id);
            if(behavior != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Behavior has been successfully fetched.");
                result.data = behavior;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Behavior does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get behavior", e);
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
            List<Behavior> behaviors = BehaviorManager.getInstance().getBehaviors();
            
            APIResult result = APIResultUtils.buildOKAPIResult("Behaviors have been successfully fetched.");
            result.list = behaviors;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get behaviors", e);
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
            Behavior behaviro = BehaviorManager.getInstance().getBehavior(id);
            
            BehaviorManager.getInstance().deleteBehavior(id);
            
            Event event = new Event();
            event.type = EventType.DeleteBehavior;
            event.data.put("data", behaviro);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Behavior has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete behavior", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/create_public")
    @Produces("application/json")
    public Response createPublic(
            @FormParam("key") String key,
            @FormParam("defId") int defId,
            @Context HttpHeaders headers,
            @Context HttpServletRequest request,
            @Context UriInfo uriInfo) throws URISyntaxException {
        String k = DataManager.getInstance().getValue("public.key");
        if(k == null || !k.equals(key)) {
            return APIResultUtils.buildURLResponse(request, "public_error.jsp");
        }
        
        try {
            Behavior behavior = new Behavior();
            behavior.defId = defId;
            behavior.time = System.currentTimeMillis();
            BehaviorManager.getInstance().createBehavior(behavior);
            
            Event event = new Event();
            event.type = EventType.CreateBehavior;
            event.data.put("data", behavior);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildURLResponse(request, "public/behavior.jsp");
        }
        catch(Exception e) {
            logger.error("failed to create behavior", e);
            return APIResultUtils.buildURLResponse(request, "public_error.jsp", e.getMessage());
        }
    }
    
    @POST
    @Path("/trend")
    @Produces("application/json")
    public Response trend(
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
        
        TimeZone tz = (TimeZone) request.getSession().getAttribute("world-timezone");
        Map<Long, BehaviorInfo> data = BehaviorManager.getInstance().getTrend(name, tz);
        List<Long> keys = new ArrayList<Long>(data.keySet());
        Collections.sort(keys);
        StringBuffer sb = new StringBuffer("[");
        if(!keys.isEmpty()) {
            long first = keys.get(0);
            long last = keys.get(keys.size() - 1);
            long key = first;
            while(key <= last) {
                BehaviorInfo info = data.get(key);
                if(info == null) {
                    info = new BehaviorInfo();
                    info.time = key;
                    info.count = 0;
                    info.init(tz);
                }
                
                sb.append("[");
                sb.append(info.timeStr);
                sb.append(",");
                sb.append(info.count);
                sb.append("]");
                
                if(key != last) {
                    sb.append(",");
                }
                
                key += TimeUtils.DAY_DURATION;
            }
        }
        sb.append("]");
        
        APIResult result = APIResultUtils.buildOKAPIResult(sb.toString());
        
        return APIResultUtils.buildJSONResponse(result);
    }
}
