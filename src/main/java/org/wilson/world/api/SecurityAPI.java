package org.wilson.world.api;

import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.api.util.APIResultUtils;
import org.wilson.world.context.ContextInitializer;
import org.wilson.world.event.Event;
import org.wilson.world.event.EventType;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.ExtManager;
import org.wilson.world.manager.NotifyManager;
import org.wilson.world.manager.QuoteManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.APIResultStatus;
import org.wilson.world.model.Quote;

@Path("/security")
public class SecurityAPI {
    private static final Logger logger = Logger.getLogger(SecurityAPI.class);
    
    @POST
    @Path("/login")
    @Produces("application/json")
    public Response login(
            @FormParam("username") String username, 
            @FormParam("password") String password,
            @FormParam("timezone") String timezone,
            @Context HttpHeaders headers,
            @Context HttpServletRequest request,
            @Context UriInfo uriInfo) {
        if(StringUtils.isEmpty(password) || StringUtils.isEmpty(password)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Username and password should be provided."));
        }
        
        username = username.trim();
        password = password.trim();
        
        String msg = SecManager.getInstance().authenticate(username, password);
        if(msg == null) {
            String uuid = SecManager.getInstance().generateToken();
            SecManager.getInstance().addToken(uuid);
            request.getSession().setAttribute("world-token", uuid);
            request.getSession().setAttribute("world-user", username);
            
            if(!StringUtils.isBlank(timezone)) {
                TimeZone tz = getClientTimeZone(timezone);
                if(tz != null) {
                    request.getSession().setAttribute("world-timezone", tz);
                    logger.info("Set client timezone to " + tz.getID());
                }
            }
            
            String message = "Welcome to WORLD";
            Quote quote = QuoteManager.getInstance().randomQuote();
            if(quote != null) {
                message = quote.content;
            }
            NotifyManager.getInstance().notifySuccess(message);
            
            ContextInitializer ci = ExtManager.getInstance().getExtension(ContextInitializer.class);
            if(ci != null) {
                ci.setCurrentContext();
            }
            
            Event event = new Event();
            event.type = EventType.Login;
            event.data.put("timezone", request.getSession().getAttribute("world-timezone"));
            EventManager.getInstance().fireEvent(event);
            
            APIResult result = new APIResult();
            result.status = APIResultStatus.OK;
            result.message = uuid;
            return APIResultUtils.buildJSONResponse(result);
        }
        else {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Login failed. " + msg));
        }
    }
    
    public static TimeZone getClientTimeZone(String tz) {
        int timeZone = Integer.parseInt(tz);
        if (timeZone >= 0) {
            tz = "+" + timeZone;
        }
         
        TimeZone timezone = TimeZone.getTimeZone("GMT" + tz);
        return timezone;
    }
}
