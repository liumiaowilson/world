package org.wilson.world.api;

import java.net.URISyntaxException;
import java.util.List;
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

import org.wilson.world.api.util.APIResultUtils;
import org.wilson.world.manager.DataManager;

@Path("/check")
public class CheckAPI {
    
    @POST
    @Path("/send")
    @Produces("application/json")
    public Response send(
            @FormParam("key") String key,
            @FormParam("id") List<String> ids,
            @FormParam("timezone") String timezone,
            @Context HttpHeaders headers,
            @Context HttpServletRequest request,
            @Context UriInfo uriInfo) throws URISyntaxException {
        String k = DataManager.getInstance().getValue("public.key");
        if(k == null || !k.equals(key)) {
            return APIResultUtils.buildURLResponse(request, "public_error.jsp");
        }
        
        TimeZone tz = SecurityAPI.getClientTimeZone(timezone);
        request.getSession().setAttribute("world-timezone", tz);
        
        if(!ids.isEmpty()) {
            HabitTraceAPI.check(tz, ids.toArray(new String[0]));
        }
        
        return APIResultUtils.buildURLResponse(request, "check.jsp");
    }
}
