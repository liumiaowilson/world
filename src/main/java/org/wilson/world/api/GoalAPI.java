package org.wilson.world.api;

import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
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
import org.wilson.world.manager.GoalManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.Goal;
import org.wilson.world.util.TimeUtils;

@Path("/goal")
public class GoalAPI {
    private static final Logger logger = Logger.getLogger(GoalAPI.class);
    
    @POST
    @Path("/report")
    @Produces("application/json")
    public Response report(
            @FormParam("defId") int defId, 
            @FormParam("time") String timeStr,
            @FormParam("amount") int amount,
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
            if(tz == null) {
                tz = TimeZone.getDefault();
            }
            long time = TimeUtils.fromDateString(timeStr, tz).getTime();
            
            Goal goal = GoalManager.getInstance().reportGoal(defId, time, amount);
            
            Event event = new Event();
            event.type = EventType.ReportGoal;
            event.data.put("data", goal);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Goal has been successfully reported."));
        }
        catch(Exception e) {
            logger.error("failed to report goal", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
