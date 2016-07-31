package org.wilson.world.api;

import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
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
import org.wilson.world.manager.HabitManager;
import org.wilson.world.manager.HabitTraceManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.Habit;
import org.wilson.world.model.HabitTrace;

@Path("/habit_trace")
public class HabitTraceAPI {
    private static final Logger logger = Logger.getLogger(HabitTraceAPI.class);
    
    @GET
    @Path("/check")
    @Produces("application/json")
    public Response check(
            @QueryParam("ids") String ids,
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
        
        if(StringUtils.isBlank(ids)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Habit ids are needed."));
        }
        
        try {
            TimeZone tz = (TimeZone) request.getSession().getAttribute("world-timezone");
            check(tz, ids.split(","));
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Habits have been successfully checked."));
        }
        catch(Exception e) {
            logger.error("failed to check habits!", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Failed to check habits."));
        }
    }
    
    public static void check(TimeZone tz, String [] ids) {
        for(String id : ids) {
            Habit habit = HabitManager.getInstance().getHabit(Integer.parseInt(id));
            if(habit != null) {
                HabitTrace trace = HabitTraceManager.getInstance().checkHabit(habit.id, tz);

                Event event = new Event();
                event.type = EventType.CheckHabit;
                event.data.put("data", trace);
                EventManager.getInstance().fireEvent(event);
            }
        }
    }
}
