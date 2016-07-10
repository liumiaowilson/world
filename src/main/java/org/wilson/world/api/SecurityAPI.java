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
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.APIResultStatus;

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
        
        String msg = SecManager.getInstance().authenticate(username, password);
        if(msg == null) {
            String uuid = SecManager.getInstance().generateToken();
            SecManager.getInstance().addToken(uuid);
            request.getSession().setAttribute("world-token", uuid);
            request.getSession().setAttribute("world-user", username);
            
            if(!StringUtils.isBlank(timezone)) {
                TimeZone tz = this.getClientTimeZone(timezone);
                if(tz != null) {
                    request.getSession().setAttribute("world-timezone", tz);
                    logger.info("Set client timezone to " + tz.getID());
                }
            }
            
            APIResult result = new APIResult();
            result.status = APIResultStatus.OK;
            result.message = uuid;
            return APIResultUtils.buildJSONResponse(result);
        }
        else {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Login failed. " + msg));
        }
    }
    
    private TimeZone getClientTimeZone(String tz) {
        int timeZone = Integer.parseInt(tz);
        if (timeZone >= 0) {
            tz = "+" + timeZone;
        }
         
        TimeZone timezone = TimeZone.getTimeZone("GMT" + tz);
        return timezone;
    }
}
