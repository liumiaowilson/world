package org.wilson.world.api;

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
import org.wilson.world.cloud.CloudStorageService;
import org.wilson.world.manager.CloudStorageManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;

@Path("cloud_storage")
public class CloudStorageAPI {
    private static final Logger logger = Logger.getLogger(CloudStorageAPI.class);
    
    @POST
    @Path("/get_sample_config_data")
    @Produces("application/json")
    public Response getSampleConfigData(
            @FormParam("name") String name,
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
        	return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Name is needed."));
        }
        
        try {
        	CloudStorageService service = CloudStorageManager.getInstance().getCloudStorageService(name);
            if(service != null) {
            	String data = service.getSampleConfigData();
            	
                APIResult result = APIResultUtils.buildOKAPIResult("CloudStorageService has been successfully fetched.");
                result.data = data;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("CloudStorageService does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get sample config data", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
}
