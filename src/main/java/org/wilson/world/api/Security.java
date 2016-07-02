package org.wilson.world.api;

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
import org.wilson.world.api.util.APIResultUtils;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.APIResultStatus;

@Path("/security")
public class Security {
    @POST
    @Path("/login")
    @Produces("application/json")
    public Response login(
            @FormParam("username") String username, 
            @FormParam("password") String password,
            @Context HttpHeaders headers,
            @Context HttpServletRequest request,
            @Context UriInfo uriInfo) {
        if(StringUtils.isEmpty(password) || StringUtils.isEmpty(password)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Username and password should be provided."));
        }
        
        if("wilson".equals(username) && "wilson".equals(password)) {
            APIResult result = new APIResult();
            result.status = APIResultStatus.OK;
            return APIResultUtils.buildJSONResponse(result);
        }
        else {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Login failed."));
        }
    }
}
