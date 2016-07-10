package org.wilson.world.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
import org.wilson.world.api.util.APIResultUtils;
import org.wilson.world.manager.SecManager;
import org.wilson.world.manager.StatsManager;
import org.wilson.world.manager.StatsManager.TrendInfo;
import org.wilson.world.model.APIResult;

@Path("/stats")
public class StatsAPI {
    @GET
    @Path("/trend")
    @Produces("application/json")
    public Response trend(
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
        Map<Long, TrendInfo> data = StatsManager.getInstance().getTrendInOneMonth(tz);
        List<Long> keys = new ArrayList<Long>(data.keySet());
        Collections.sort(keys);
        StringBuffer sb = new StringBuffer("[");
        for(int i = 0; i < keys.size(); i++) {
            long key = keys.get(i);
            TrendInfo info = data.get(key);
            sb.append("[");
            sb.append(info.timeStr);
            sb.append(",");
            sb.append(info.count);
            sb.append("]");
            
            if(i != keys.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        
        APIResult result = APIResultUtils.buildOKAPIResult(sb.toString());
        
        return APIResultUtils.buildJSONResponse(result);
    }
}
