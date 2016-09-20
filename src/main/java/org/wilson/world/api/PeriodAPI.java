package org.wilson.world.api;

import java.util.Date;
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
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.PeriodManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.Period;
import org.wilson.world.util.TimeUtils;

@Path("period")
public class PeriodAPI {
    private static final Logger logger = Logger.getLogger(PeriodAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("status") String status, 
            @FormParam("time") String timeStr,
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
        
        if(StringUtils.isBlank(status)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Period status should be provided."));
        }
        status = status.trim();
        if(StringUtils.isBlank(timeStr)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Period time should be provided."));
        }
        timeStr = timeStr.trim();
        
        try {
            TimeZone tz = (TimeZone)request.getSession().getAttribute("world-timezone");
            Date date = TimeUtils.fromDateString(timeStr, tz);
            
            Period period = new Period();
            period.status = status;
            period.time = date.getTime();
            PeriodManager.getInstance().createPeriod(period);
            
            Event event = new Event();
            event.type = EventType.CreatePeriod;
            event.data.put("data", period);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Period has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create period", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("status") String status, 
            @FormParam("time") String timeStr,
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
        
        if(StringUtils.isBlank(status)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Period name should be provided."));
        }
        status = status.trim();
        if(StringUtils.isBlank(timeStr)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Period time should be provided."));
        }
        timeStr = timeStr.trim();
        
        try {
            TimeZone tz = (TimeZone)request.getSession().getAttribute("world-timezone");
            Date date = TimeUtils.fromDateString(timeStr, tz);
            
            Period oldPeriod = PeriodManager.getInstance().getPeriod(id);
            
            Period period = new Period();
            period.id = id;
            period.status = status;
            period.time = date.getTime();
            PeriodManager.getInstance().updatePeriod(period);
            
            Event event = new Event();
            event.type = EventType.UpdatePeriod;
            event.data.put("old_data", oldPeriod);
            event.data.put("new_data", period);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Period has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update period", e);
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
            Period period = PeriodManager.getInstance().getPeriod(id);
            if(period != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Period has been successfully fetched.");
                result.data = period;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Period does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get period", e);
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
            List<Period> periods = PeriodManager.getInstance().getPeriods();
            
            APIResult result = APIResultUtils.buildOKAPIResult("Periods have been successfully fetched.");
            result.list = periods;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get periods", e);
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
            Period period = PeriodManager.getInstance().getPeriod(id);
            
            PeriodManager.getInstance().deletePeriod(id);
            
            Event event = new Event();
            event.type = EventType.DeletePeriod;
            event.data.put("data", period);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Period has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete period", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
