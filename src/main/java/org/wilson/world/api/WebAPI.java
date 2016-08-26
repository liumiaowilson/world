package org.wilson.world.api;

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
import org.wilson.world.manager.SecManager;
import org.wilson.world.manager.WebManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.web.DataSizeItem;
import org.wilson.world.web.WebJob;
import org.wilson.world.web.WordInfo;

@Path("/web")
public class WebAPI {
    private static final Logger logger = Logger.getLogger(WebAPI.class);
    
    @POST
    @Path("/lookup")
    @Produces("application/json")
    public Response lookup(
            @FormParam("word") String word,
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
            if(StringUtils.isBlank(word)) {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Word is not provided."));
            }
            word = word.trim();
            word = word.replaceAll(" ", "%20");
            
            WordInfo info = WebManager.getInstance().lookup(word);
            if(info != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Word has been successfully looked up.");
                result.data = info;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Cannot look up the word."));
            }
        }
        catch(Exception e) {
            logger.error("failed to look up word", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/stop")
    @Produces("application/json")
    public Response stop(
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
            WebJob job = WebManager.getInstance().getWebJob(id);
            String ret = WebManager.getInstance().stop(job);
            
            if(ret == null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Web job has been successfully stopped.");
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(ret));
            }
        }
        catch(Exception e) {
            logger.error("failed to stop web job", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/trend")
    @Produces("application/json")
    public Response trend(
            @QueryParam("name") String name,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Name should be provided."));
        }
        
        TimeZone tz = (TimeZone) request.getSession().getAttribute("world-timezone");
        List<DataSizeItem> items = WebManager.getInstance().getDataSizeTrend(name, tz);
        StringBuffer sb = new StringBuffer("[");
        for(int i = 0; i < items.size(); i++) {
            DataSizeItem item = items.get(i);
            sb.append("[");
            sb.append(item.display);
            sb.append(",");
            sb.append(item.count);
            sb.append("]");
            
            if(i != items.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        
        APIResult result = APIResultUtils.buildOKAPIResult(sb.toString());
        
        return APIResultUtils.buildJSONResponse(result);
    }
}
