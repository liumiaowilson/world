package org.wilson.world.api;

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
import org.wilson.world.manager.ReminderManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.Reminder;

@Path("reminder")
public class ReminderAPI {
    private static final Logger logger = Logger.getLogger(ReminderAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("message") String message,
            @FormParam("hours") int hours,
            @FormParam("minutes") int minutes,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Reminder name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(message)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Reminder message should be provided."));
        }
        message = message.trim();
        
        try {
            Reminder reminder = new Reminder();
            reminder.name = name;
            reminder.message = message;
            reminder.time = System.currentTimeMillis();
            reminder.hours = hours;
            reminder.minutes = minutes;
            ReminderManager.getInstance().createReminder(reminder);
            
            Event event = new Event();
            event.type = EventType.CreateReminder;
            event.data.put("data", reminder);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Reminder has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create reminder", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("name") String name, 
            @FormParam("message") String message,
            @FormParam("hours") int hours,
            @FormParam("minutes") int minutes,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Reminder name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(message)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Reminder message should be provided."));
        }
        message = message.trim();
        
        try {
            Reminder oldReminder = ReminderManager.getInstance().getReminder(id);
            
            Reminder reminder = new Reminder();
            reminder.id = id;
            reminder.name = name;
            reminder.message = message;
            reminder.time = System.currentTimeMillis();
            reminder.hours = hours;
            reminder.minutes = minutes;
            ReminderManager.getInstance().updateReminder(reminder);
            
            Event event = new Event();
            event.type = EventType.UpdateReminder;
            event.data.put("old_data", oldReminder);
            event.data.put("new_data", reminder);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Reminder has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update reminder", e);
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
            Reminder reminder = ReminderManager.getInstance().getReminder(id);
            if(reminder != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Reminder has been successfully fetched.");
                result.data = reminder;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Reminder does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get reminder", e);
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
            List<Reminder> reminders = ReminderManager.getInstance().getReminders();
            
            for(Reminder reminder : reminders) {
                reminder.remainingTime = ReminderManager.getInstance().getRemainingTimeDisplay(reminder);
            }
            
            APIResult result = APIResultUtils.buildOKAPIResult("Reminders have been successfully fetched.");
            result.list = reminders;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get reminders", e);
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
            Reminder reminder = ReminderManager.getInstance().getReminder(id);
            
            ReminderManager.getInstance().deleteReminder(id);
            
            Event event = new Event();
            event.type = EventType.DeleteReminder;
            event.data.put("data", reminder);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Reminder has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete reminder", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}