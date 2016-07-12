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
import org.wilson.world.manager.ExtManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.ExtensionPoint;

@Path("extension_point")
public class ExtensionPointAPI {
    private static final Logger logger = Logger.getLogger(ExtensionPointAPI.class);
    
    @GET
    @Path("/get")
    @Produces("application/json")
    public Response get(
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
        
        try {
            ExtensionPoint ep = ExtManager.getInstance().getExtensionPoint(name);
            if(ep != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Extension point is successfully fetched.");
                result.data = ep;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Extension point does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get extension point", e);
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
            List<ExtensionPoint> eps = ExtManager.getInstance().getExtensionPoints();
            
            APIResult result = APIResultUtils.buildOKAPIResult("Extension points have been successfully fetched.");
            result.list = eps;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get extension points", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/bind")
    @Produces("application/json")
    public Response bind(
            @FormParam("ext_name") String ext_name,
            @FormParam("action_name") String action_name,
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
        if(StringUtils.isBlank(ext_name)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Extension name should be provided."));
        }
        ext_name = ext_name.trim();
        if(StringUtils.isBlank(action_name)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Action name should be provided."));
        }
        action_name = action_name.trim();
        
        try {
            ExtManager.getInstance().bindAction(ext_name, action_name);
            
            APIResult result = APIResultUtils.buildOKAPIResult("Extension point has been successfully bound.");
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to bind extension point", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/unbind")
    @Produces("application/json")
    public Response unbind(
            @FormParam("ext_name") String ext_name,
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
        if(StringUtils.isBlank(ext_name)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Extension name should be provided."));
        }
        ext_name = ext_name.trim();
        
        try {
            ExtManager.getInstance().unbindAction(ext_name);
            
            APIResult result = APIResultUtils.buildOKAPIResult("Extension point has been successfully unbound.");
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to unbind extension point", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}
