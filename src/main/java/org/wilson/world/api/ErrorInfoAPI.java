package org.wilson.world.api;

import java.util.List;

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
import org.wilson.world.manager.ErrorInfoManager;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.MonitorManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.ErrorInfo;

@Path("error_info")
public class ErrorInfoAPI {
    private static final Logger logger = Logger.getLogger(ErrorInfoAPI.class);
    
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
            ErrorInfo info = ErrorInfoManager.getInstance().getErrorInfo(id);
            if(info != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Error ifo has been successfully fetched.");
                result.data = info;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Error info does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get error info", e);
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
            List<ErrorInfo> infos = ErrorInfoManager.getInstance().getErrorInfos();
            
            APIResult result = APIResultUtils.buildOKAPIResult("Error infos have been successfully fetched.");
            result.list = infos;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get error infos", e);
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
            ErrorInfo info = ErrorInfoManager.getInstance().getErrorInfo(id);
            
            ErrorInfoManager.getInstance().deleteErrorInfo(id);
            
            Event event = new Event();
            event.type = EventType.DeleteErrorInfo;
            event.data.put("data", info);
            EventManager.getInstance().fireEvent(event);
            
            List<ErrorInfo> infos = ErrorInfoManager.getInstance().getErrorInfos();
            if(infos.isEmpty()) {
                MonitorManager.getInstance().removeAlert(ErrorInfoManager.getInstance().getMonitor().getAlert());
            }
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Error info has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete error info", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
