package org.wilson.world.api;

import java.net.URISyntaxException;
import java.util.List;
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
import org.wilson.world.event.Event;
import org.wilson.world.event.EventType;
import org.wilson.world.manager.DataManager;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.manager.SleepManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.Sleep;
import org.wilson.world.util.TimeUtils;

@Path("sleep")
public class SleepAPI {
    private static final Logger logger = Logger.getLogger(SleepAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("startTime") String startTimeStr,
            @FormParam("endTime") String endTimeStr,
            @FormParam("quality") int quality,
            @FormParam("dreams") int dreams,
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
        
        if(StringUtils.isBlank(startTimeStr)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Start time should be provided."));
        }
        startTimeStr = startTimeStr.trim();
        if(StringUtils.isBlank(endTimeStr)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("End time should be provided."));
        }
        endTimeStr = endTimeStr.trim();
        
        try {
            TimeZone tz = (TimeZone)request.getSession().getAttribute("world-timezone");
            long startTime = TimeUtils.fromDateTimeString(startTimeStr, tz).getTime();
            long endTime = TimeUtils.fromDateTimeString(endTimeStr, tz).getTime();
            
            Sleep sleep = new Sleep();
            sleep.startTime = startTime;
            sleep.endTime = endTime;
            sleep.quality = quality;
            sleep.dreams = dreams;
            SleepManager.getInstance().createSleep(sleep);
            
            Event event = new Event();
            event.type = EventType.CreateSleep;
            event.data.put("data", sleep);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Sleep has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create sleep", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("startTime") String startTimeStr,
            @FormParam("endTime") String endTimeStr,
            @FormParam("quality") int quality,
            @FormParam("dreams") int dreams,
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
            TimeZone tz = (TimeZone)request.getSession().getAttribute("world-timezone");
            long startTime = TimeUtils.fromDateTimeString(startTimeStr, tz).getTime();
            long endTime = TimeUtils.fromDateTimeString(endTimeStr, tz).getTime();
            
            Sleep oldSleep = SleepManager.getInstance().getSleep(id);
            
            Sleep sleep = new Sleep();
            sleep.id = id;
            sleep.startTime = startTime;
            sleep.endTime = endTime;
            sleep.quality = quality;
            sleep.dreams = dreams;
            SleepManager.getInstance().updateSleep(sleep);
            
            Event event = new Event();
            event.type = EventType.UpdateSleep;
            event.data.put("old_data", oldSleep);
            event.data.put("new_data", sleep);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Sleep has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update sleep", e);
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
            TimeZone tz = (TimeZone)request.getSession().getAttribute("world-timezone");
            Sleep sleep = SleepManager.getInstance().getSleep(id);
            sleep = SleepManager.getInstance().loadSleep(sleep, tz);
            if(sleep != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Sleep has been successfully fetched.");
                result.data = sleep;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Sleep does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get sleep", e);
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
            TimeZone tz = (TimeZone)request.getSession().getAttribute("world-timezone");
            List<Sleep> sleeps = SleepManager.getInstance().getSleeps();
            for(Sleep sleep : sleeps) {
                sleep = SleepManager.getInstance().loadSleep(sleep, tz);
            }
            
            APIResult result = APIResultUtils.buildOKAPIResult("Sleeps have been successfully fetched.");
            result.list = sleeps;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get sleeps", e);
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
            Sleep sleep = SleepManager.getInstance().getSleep(id);
            
            SleepManager.getInstance().deleteSleep(id);
            
            Event event = new Event();
            event.type = EventType.DeleteSleep;
            event.data.put("data", sleep);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Sleep has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete sleep", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/start_sleep")
    @Produces("application/json")
    public Response startSleep(
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
            String ret = SleepManager.getInstance().startSleep();
            if(ret == null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Sleep has been successfully started.");
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(ret));
            }
        }
        catch(Exception e) {
            logger.error("failed to start sleep", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/end_sleep")
    @Produces("application/json")
    public Response endSleep(
            @FormParam("quality") int quality,
            @FormParam("dreams") int dreams,
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
            String ret = SleepManager.getInstance().endSleep(quality, dreams);
            if(ret == null) {
                Sleep sleep = SleepManager.getInstance().getLastCreatedSleep();
                
                Event event = new Event();
                event.type = EventType.CreateSleep;
                event.data.put("data", sleep);
                EventManager.getInstance().fireEvent(event);
                
                APIResult result = APIResultUtils.buildOKAPIResult("Sleep has been successfully ended.");
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(ret));
            }
        }
        catch(Exception e) {
            logger.error("failed to end sleep", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/start_sleep_public")
    @Produces("application/json")
    public Response startSleepPublic(
            @FormParam("key") String key,
            @Context HttpHeaders headers,
            @Context HttpServletRequest request,
            @Context UriInfo uriInfo) throws URISyntaxException {
        String k = DataManager.getInstance().getValue("public.key");
        if(k == null || !k.equals(key)) {
            return APIResultUtils.buildURLResponse(request, "public_error.jsp");
        }
        
        try {
            String ret = SleepManager.getInstance().startSleep();
            if(ret == null) {
                return APIResultUtils.buildURLResponse(request, "public/start_sleep.jsp");
            }
            else {
                return APIResultUtils.buildURLResponse(request, "public_error.jsp", ret);
            }
        }
        catch(Exception e) {
            logger.error("failed to start sleep", e);
            return APIResultUtils.buildURLResponse(request, "public_error.jsp", e.getMessage());
        }
    }
    
    @POST
    @Path("/end_sleep_public")
    @Produces("application/json")
    public Response endSleepPublic(
            @FormParam("key") String key,
            @FormParam("quality") int quality,
            @FormParam("dreams") int dreams,
            @Context HttpHeaders headers,
            @Context HttpServletRequest request,
            @Context UriInfo uriInfo) throws URISyntaxException {
        String k = DataManager.getInstance().getValue("public.key");
        if(k == null || !k.equals(key)) {
            return APIResultUtils.buildURLResponse(request, "public_error.jsp");
        }
        
        try {
            String ret = SleepManager.getInstance().endSleep(quality, dreams);
            if(ret == null) {
                return APIResultUtils.buildURLResponse(request, "public/end_sleep.jsp");
            }
            else {
                return APIResultUtils.buildURLResponse(request, "public_error.jsp", ret);
            }
        }
        catch(Exception e) {
            logger.error("failed to end sleep", e);
            return APIResultUtils.buildURLResponse(request, "public_error.jsp", e.getMessage());
        }
    }
}
